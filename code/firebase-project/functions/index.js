const functions = require('firebase-functions');

const admin = require('firebase-admin');

const serviceAccount = require('./arvideocall-38d60-firebase-adminsdk-jokf6-dbb11cccc4.json');

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    storageBucket: 'gs://arvideocall-38d60.appspot.com'
});

exports.onCreateNewUser = functions.auth.user().onCreate(async (user) => {

    // Copy profile image to storage if exists
    if(user.photoURL !== null){
        const axios = require('axios');
        axios.get(user.photoURL, {responseType: 'arraybuffer'})
            .then(response => {
                const imageFile = admin.storage().bucket().file('users/' + user.uid + '/images/profile_photo.png');
                const options = {
                    metadata: {contentType: 'image/png'}
                };

                imageFile.save(response.data, options)
                    .then(() => console.log('Done'))
                    .catch((e) => console.log('Upload fail ' + e.toString()));
                return null;
            })
            .catch(e => console.log('Upload image profile failed: ' + e.toString()));
    }

    // Add new user to the database
    const userRef = admin.firestore().collection('users_data').doc(user.uid);
    return userRef.set({
        name: user.displayName === null ? "" : user.displayName.split(' ')[0],
        surname: user.displayName === null ? "" : user.displayName.split(' ')[1],
        email: user.email,
        nickname: user.email,
        profileImageCount: user.photoURL === null ? -1 : 0
    }).then(() => console.log('New user created'))
        .catch(e => 'Creation failed: ' + e);
});