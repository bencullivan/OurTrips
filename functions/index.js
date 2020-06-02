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

/**
 * These functions delete the sub collections of a user when the document corresponding 
 * to a user is deleted
 * @author Ben Cullivan
 */
exports.onUserTripsDeleted = functions.firestore.document('users/{user}').onDelete((snap) => {
    // get the id of the document that was deleted
    const id = snap.id;
    const userTripsPath = 'users/'+id+'/user_trips';

     // delete the trips sub-collection
     return tools.firestore.delete(userTripsPath, {
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
});
exports.onUserFriendsDeleted = functions.firestore.document('users/{user}').onDelete((snap) => {
    // get the id of the document that was deleted
    const id = snap.id;
    const userFriendsPath = 'users/'+id+'/user_friends';

    // delete the friends sub-collection
    return tools.firestore.delete(userFriendsPath, {
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
});
exports.onUserFriendReqsDeleted = functions.firestore.document('users/{user}').onDelete((snap) => {
    // get the id of the document that was deleted
    const id = snap.id;
    const userRequestsPath = 'users/'+id+'friend_requests';

    // delete the requests sub collections
    return tools.firestore.delete(userRequestsPath, {
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
});




/**
 * These functions delete the trippers, comments, locations and photos sub-collections corresponding 
 * to a trip when a document corresponding to a trip is deleted
 * @author Ben Cullivan
 */
exports.deleteTrippers = functions.firestore.document('trips/{trip}').onDelete((snap) => {
    // get the id of the document that was deleted
    const id = snap.id;
    const trippersPath = 'trips/'+id+'/trippers';

    // delete the trippers sub-collection
    return tools.firestore.delete(trippersPath, {
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
});
exports.deletePhotos = functions.firestore.document('trips/{trip}').onDelete((snap) => {
  // get the id of the document that was deleted
  const id = snap.id;
  const photosPath = 'trips/'+id+'/photos';

  // delete the photos sub-collection
  return tools.firestore.delete(photosPath, {
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
});
exports.deleteComments = functions.firestore.document('trips/{trip}').onDelete((snap) => {
  // get the id of the document that was deleted
  const id = snap.id;
  const commentsPath = 'trips/'+id+'/comments';

  // delete the comments sub-collection
  return tools.firestore.delete(commentsPath, {
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
});
exports.deleteLocations = functions.firestore.document('trips/{trip}').onDelete((snap) => {
  // get the id of the document that was deleted
  const id = snap.id;
  const locationsPath = 'trips/'+id+'/locations';

  // delete the locations sub-collection
  return tools.firestore.delete(locationsPath, {
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
});