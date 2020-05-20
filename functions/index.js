const functions = require('firebase-functions');
const tools = require('firebase-tools');
const admin = require("firebase-admin");

// initialize app
admin.initializeApp({
  credential: admin.credential.applicationDefault()
});

// get a reference to firestore
const db = admin.firestore();
// get a reference to storage
const bucket = admin.storage().bucket("gs://our-trips-74b79.appspot.com");

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
function compareDates(date1, date2) {
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
  .firestore.document("users/{user}").onDelete((snap) => {
    // get the id of the document that was deleted
    const id = snap.id;

    // if there is data within the document (This should always be true)
    if (snap.data() !== undefined) {
      // get the path of the profile picture
      const profilePath = snap.data().profile_pic_path;
      console.log(profilePath);
      // delete the profile picture from storage
      if (profilePath !== undefined) bucket.file(profilePath).delete();
    }

    // establish the paths of the sub-collections
    const userFriendsPath = "users/"+id+"/user_friends";
    const userTripsPath = "users/"+id+"/user_trips";

    // remove this user from the friends list of all their friends
    db.collection(userFriendsPath).get()
      .then(snapshot => {
        snapshot.forEach(friend => {
          db.collection("users")
            .doc(friend.id)
            .collection("user_friends")
            .doc(id)
            .delete();
        });
        return snapshot;
      })
      .catch(err => {
        console.log("Error removing from friends: " + err);
      });

    // delete the trips sub-collection
    tools.firestore.delete(userTripsPath, {
        project: process.env.GCLOUD_PROJECT,
        recursive: true,
        yes: true,
        token: functions.config().fb.token
      })
      .then(() => {
        return {
          path: userTripsPath
        };
      })
      .catch(err => {
        console.log(err);
      });

    // delete the friends sub-collection
    tools.firestore.delete(userFriendsPath, {
        project: process.env.GCLOUD_PROJECT,
        recursive: true,
        yes: true,
        token: functions.config().fb.token
      })
      .then(() => {
        return {
          path: userFriendsPath 
        };
      })
      .catch(err => {
        console.log(err);
      });
    
    return 1;
});


/**
 * onTripDeleted
 * triggered when the document associated with a trip is deleted,
 * this function deletes all of the photos from the trips photo album from the 
 * cloud storage bucket. It also removes this trip from each trippers' list of 
 * trips. It also deletes the trippers, comments, and photos sub-collections
 * @author Ben Cullivan
 * @param {DocumentSnapshot} snap - a snapshot associated with the document that is being deleted
 */
exports.onTripDeleted = functions.runWith({timeoutSeconds: 540, memory: '2GB'})
  .firestore.document("trips/{trip}").onDelete((snap) => {
    // get the id of the document that was deleted
    const id = snap.id;

    // establish the paths of the sub-collections
    const commentsPath = "trips/"+id+"/comments";
    const trippersPath = "trips/"+id+"/trippers";
    const photosPath = "trips/"+id+"/photos";

    // delete all of the photos from the storage bucket
    db.collection(photosPath).get()
      .then(snapshot => {
        snapshot.forEach(photo => {
          bucket.file(photo.id).delete();
        });
        return snapshot;
      })
      .catch(err => {
        console.log("Error removing photos: " + err);
      });

    // delete this trip from each trippers' list of trips
    db.collection(trippersPath).get()
      .then(snapshot => {
        snapshot.forEach(user => {
          db.collection("users")
            .doc(user.id)
            .collection("user_trips")
            .doc(id)
            .delete();
        });
        return snapshot;
      })
      .catch(err => {
        console.log("Error removing trip from users: " + err);
      });
    
    // delete the comments sub-collection
    tools.firestore.delete(commentsPath, {
      project: process.env.GCLOUD_PROJECT,
      recursive: true,
      yes: true,
      token: functions.config().fb.token
    })
    .then(() => {
      return {
        path: commentsPath
      };
    })
    .catch(err => {
      console.log(err);
    });

    // delete the photos sub-collection
    tools.firestore.delete(photosPath, {
      project: process.env.GCLOUD_PROJECT,
      recursive: true,
      yes: true,
      token: functions.config().fb.token
    })
    .then(() => {
      return {
        path: photosPath
      };
    })
    .catch(err => {
      console.log(err);
    });

    // delete the trippers sub-collection
    tools.firestore.delete(trippersPath, {
      project: process.env.GCLOUD_PROJECT,
      recursive: true,
      yes: true,
      token: functions.config().fb.token
    })
    .then(() => {
      return {
        path: trippersPath
      };
    })
    .catch(err => {
      console.log(err);
    });
    
    return 1;
});