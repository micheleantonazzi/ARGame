const functions = require('firebase-functions');

const admin = require('firebase-admin');

admin.initializeApp();

exports.onCreateNewUser = functions.auth.user().onCreate(async (user) => {
    const database = admin.firestore();

    // Add new user to the database
    const userRef = database.collection('users').doc(user.uid);

    await userRef.set({
        name: user.displayName === null ? "" : user.displayName.split(' ')[0],
        surname: user.displayName === null ? "" : user.displayName.split(' ')[1],
        email: user.email,
        nickname: "",
        profile_image: user.photoURL === null ? -1 : 0
    });
});
