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
 * compareDates
 * helper function to compare two string dates
 * @author Ben Cullivan
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


/**
 * onUserDeleted
 * triggered when the document associated with a user is deleted,
 * this function deletes the user from the user_friends sub collection of all of their friends
 * it also deletes the user_friends and user_trips sub collections of this user
 * @author Ben Cullivan
 * @param {DocumentSnapshot} snap - a snapshot associated with the document that is being deleted
 */
exports.onUserDeleted = functions.runWith({timeoutSeconds: 540, memory: '2GB'})
  .firestore.document('users/{user}').onDelete((snap) => {
    // get the id of the document that was deleted
    const id = snap.id;

    // establish the paths of the sub-collections
    const path1 = "users/"+id+"/user_friends";
    const path2 = "users/"+id+"/user_trips";

    // remove this user from the friends list of all their friends
    db.collection(path1).get()
      .then(snapshot => {
        snapshot.forEach(friend => {
          db.collection("users").doc(friend.id).collection("user_friends").doc(id).delete();
        });
        return snapshot;
      })
      .catch(err => {
        console.log("error removing from friends", err);
      })

    
    // delete the trips sub-collection
    firebase_tools.firestore.delete(path2, {
        project: process.env.GCLOUD_PROJECT,
        recursive: true,
        yes: true,
        token: functions.config().fb.token
      })
      .then(() => {
        return {
          path: path2 
        };
      })
      .catch(err => {
        console.log(err);
      });

    // delete the friends sub-collection
    firebase_tools.firestore.delete(path1, {
        project: process.env.GCLOUD_PROJECT,
        recursive: true,
        yes: true,
        token: functions.config().fb.token
      })
      .then(() => {
        return {
          path: path1 
        };
      })
      .catch(err => {
        console.log(err);
      });

    return 1;
});