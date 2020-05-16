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
   * custom function to find the dates that two users are both available
   * @author Ben Cullivan
   */
exports.matchDates = functions.https.onCall((data) => {
  
  // get the user ids
  const user1 = data.user1;
  const user2 = data.user2;

  // get the document corresponding to each user
  let doc1Ref = db.collection("users").doc(user1)
  let doc1 = doc1Ref.get().then(doc => {
    if (doc.exists) {
      console.log('Document data:', doc.data());
      return doc.data();
    } else {
      throw new Error("document does not exist")
    }
  }).catch(err => {
    console.log('Error getting document', err);
  });

  let doc2Ref = db.collection("users").doc(user2)
  let doc2 = doc2Ref.get().then(doc => {
    if (doc.exists) {
      console.log('Document data:', doc.data());
      return doc.data();
    } else {
      throw new Error("document does not exist")
    }
  }).catch(err => {
    console.log('Error getting document', err);
  });

  // get the list of times the users are available


});
