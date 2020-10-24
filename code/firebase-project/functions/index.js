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
        uid: user.uid,
        name: user.displayName === null ? "" : user.displayName.split(' ')[0],
        surname: user.displayName === null ? "" : user.displayName.split(' ')[1],
        email: user.email,
        nickname: user.email,
        profileImageCount: user.photoURL === null ? -1 : 0
    }).then(() => console.log('New user created'))
        .catch(e => 'Creation failed: ' + e);
});



const {RtcTokenBuilder, RtmTokenBuilder, RtcRole, RtmRole} = require('agora-access-token')

const appID = '29740b29ac4d480e9ff663b48521191b';
const appCertificate = '265338c4f1b345f2b7df6bf792d1969c';
const role = RtcRole.PUBLISHER;

exports.createAgoraToken = functions.https.onCall((data, context) => {

    // Check user authentication
    if (!context.auth)
        throw new functions.https.HttpsError('failed-precondition', 'The function must be called ' +
            'while authenticated.');

    // Check data input
    if(data.channel_name === null || typeof data.channel_name !== "string" || data.channel_name.length === 0)
        throw new functions.https.HttpsError('invalid-argument', 'The function must be called with ' +
            'one arguments "channel_name" containing the agora\'s channel identifier.');

    if(data.uid === null || typeof data.uid !== "number" || data.uid === 0)
        throw new functions.https.HttpsError('invalid-argument', 'The function must be called with ' +
            'one arguments "uid" containing the agora user ID: it is the hashcode of the Google Authentication ID.');

    const expirationTimeInSeconds = 3600;
    const currentTimestamp = Math.floor(Date.now() / 1000);
    const privilegeExpiredTs = currentTimestamp + expirationTimeInSeconds;

    const token = RtcTokenBuilder.buildTokenWithUid(appID, appCertificate, data.channel_name, data.uid, role, privilegeExpiredTs);
    return {token: token}
});

