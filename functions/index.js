const functions = require('firebase-functions');
const tools = require('firebase-tools');
const admin = require('firebase-admin');

// initialize app
admin.initializeApp({
  credential: admin.credential.applicationDefault()
});

// get a reference to firestore
const db = admin.firestore();
// get a reference to storage
const bucket = admin.storage().bucket('gs://our-trips-74b79.appspot.com');

// DELETION 

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

    // if there is data within the document (This should always be true)
    if (snap.data() !== undefined) {
      // get the path of the profile picture
      const profilePath = snap.get('profile_pic_path');
      // delete the profile picture from storage
      if (profilePath !== undefined) bucket.file(profilePath).delete();
    }

    // establish the paths of the sub-collections
    const userFriendsPath = 'users/'+id+'/user_friends';
    const userTripsPath = 'users/'+id+'/user_trips';
    const userRequestsPath = 'users/'+id+'friend_requests';

    // remove this user from the friends sub-collection of all their friends
    db.collection(userFriendsPath).get()
      .then(snapshot => {
        snapshot.forEach(friend => {
          db.collection('users')
            .doc(friend.id)
            .collection('user_friends')
            .doc(id)
            .delete();
        });
        return snapshot;
      })
      .catch(err => {
        console.log('Error removing from friends: ' + err);
      });

    // remove this user from the trippers sub-collection of all of their trips
    db.collection(userTripsPath).get()
      .then(snapshot => {
        snapshot.forEach(trip => {
          db.collection('trips')
            .doc(trip.id)
            .collection('trippers')
            .doc(id)
            .delete();
        });
        return snapshot;
      })
      .catch(err => {
        console.log('Error removing from trips: ' + err);
      })

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

    // delete the friend requests sub-collection
    tools.firestore.delete(userRequestsPath, {
      project: process.env.GCLOUD_PROJECT,
      recursive: true,
      yes: true,
      token: functions.config().fb.token
    })
    .then(() => {
      return {
        path: userRequestsPath 
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
  .firestore.document('trips/{trip}').onDelete((snap) => {
    // get the id of the document that was deleted
    const id = snap.id;

    // establish the paths of the sub-collections
    const commentsPath = 'trips/'+id+'/comments';
    const trippersPath = 'trips/'+id+'/trippers';
    const photosPath = 'trips/'+id+'/photos';
    const locationsPath = 'trips/'+id+'/locations';

    // delete all of the photos from the storage bucket
    db.collection(photosPath).get()
      .then(snapshot => {
        snapshot.forEach(photo => {
          bucket.file(photo.get('photo')).delete();
        });
        return snapshot;
      })
      .catch(err => {
        console.log('Error removing photos: ' + err);
      });

    // delete this trip from each trippers' list of trips
    db.collection(trippersPath).get()
      .then(snapshot => {
        snapshot.forEach(user => {
          db.collection('users')
            .doc(user.id)
            .collection('user_trips')
            .doc(id)
            .delete();
        });
        return snapshot;
      })
      .catch(err => {
        console.log('Error removing trip from users: ' + err);
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

    // delete the locations sub-collection
    tools.firestore.delete(locationsPath, {
      project: process.env.GCLOUD_PROJECT,
      recursive: true,
      yes: true,
      token: functions.config().fb.token
    })
    .then(() => {
      return {
        path: locationsPath
      };
    })
    .catch(err => {
      console.log(err);
    });
    
    return 1;
});