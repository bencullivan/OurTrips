const functions = require('firebase-functions');
const firebase_tools = require('firebase-tools');
const admin = require("firebase-admin");

// initialize app
admin.initializeApp({
  credential: admin.credential.applicationDefault()
});

// get a reference to firestore
const db = admin.firestore();

/**
 * NOTE:
 * recursiveDelete is provided by google at:
 * https://firebase.google.com/docs/firestore/solutions/delete-collections
 * 
 * It has been modified for functionality
 */

/**
 * Initiate a recursive delete of documents at a given path.
 * 
 * The calling user must be authenticated and have the custom "admin" attribute
 * set to true on the auth token.
 * 
 * This delete is NOT an atomic operation and it's possible
 * that it may fail after only deleting some documents.
 * 
 * @param {string} data.path the document or collection path to delete.
 */
exports.recursiveDelete = functions
  .runWith({
    timeoutSeconds: 540,
    memory: '2GB'
  })
  .https.onCall((path) => {
    // // Only allow admin users to execute this function.
    // if (!(context.auth && context.auth.token && context.auth.token.admin)) {
    //   throw new functions.https.HttpsError(
    //     'permission-denied',
    //     'Must be an administrative user to initiate delete.'
    //   );
    // }

    // Run a recursive delete on the given document or collection path.
    // The 'token' must be set in the functions config, and can be generated
    // at the command line by running 'firebase login:ci'.
    return firebase_tools.firestore
      .delete(path, {
        project: process.env.GCLOUD_PROJECT,
        recursive: true,
        yes: true,
        token: functions.config().fb.token
      })
      .then(() => {
        return {
          path: path 
        };
      });
  });

  /**
   * matchDates
   * custom cloud function to find the dates that two users are both available
   * @author Ben Cullivan
   * @param {object} data - a map that contains the user ids of the two users whose dates will be compared
   * @returns a list of all the dates that the two users have in common
   */
exports.matchDates = functions.https.onCall((data) => {
  // get the date lists
  const list1 = data.list1;
  const list2 = data.list2;

  // initialize the list to hold the shared dates
  let matchedDates = [];

  // loop over the lists of dates and add the shared dates to the list of shared dates
  let a = 0;
  let b = 0;
  while (a < list1.length && b < list2.length) {
    let result = compareDates(list1[a], list2[b]);
    if (result === -1) a++;
    else if (result === 1) b++;
    else {
      matchedDates.push(list1[a]);
      a++;
      b++;
    }
  }

  // return the list of shared dates
  return matchedDates
});

/**
 * compareDates()
 * @author Ben Cullivan
 * helper function to compare two string dates
 * @param {string} date1 
 * @param {string} date2 
 * @returns -1 if date1 < date2, 0 if date1 == date2, 1 if date1 > date2
 */
let compareDates = (date1, date2) => {
    // get lists of the components of each date mm-dd-YYYY
    // they will always be of length 3
    let date1nums = date1.split("-");
    let date2nums = date2.split("-");

    // convert the strings to ints
    for (let i = 0; i < 3; i++) {
      date1nums[i] = parseInt(date1nums[i]);
      date2nums[i] = parseInt(date2nums[i]);
    }

    // compare the dates
    if (date1nums[2] < date2nums[2]) return -1;
    else if (date1nums[2] > date2nums[2]) return 1;
    else {
      if (date1nums[0] < date2nums[0]) return -1;
      else if (date1nums[0] > date2nums[0]) return 1;
      else {
        if (date1nums[1] < date2nums[1]) return -1;
        else if (date1nums[1] > date2nums[1]) return 1;
        else return 0;
      }
    }
}
