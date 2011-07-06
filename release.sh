#!/bin/sh

set -e
set -u
set -v

TEMPAPK=`tempfile -s .apk`
KEYSTORE="./android-release-key.keystore"
RELEASE="bin/PhraseDroid-release.apk"

## Delete old release
rm -f $RELEASE

## build the unsigned apk
ant release
mv bin/PhraseDroid-unsigned.apk $TEMPAPK

## sign the application with my release key
jarsigner -verbose -keystore $KEYSTORE $TEMPAPK phrasedroid
## zipalign the application
zipalign 4 $TEMPAPK $RELEASE
## Check the signature
jarsigner -verify $RELEASE
