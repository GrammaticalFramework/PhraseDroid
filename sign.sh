#!/bin/sh

set -e
set -u

TEMPPGF=`tempfile -s .apk`

ant release
cp bin/PhraseDroid-unsigned.apk $TEMPPGF
jarsigner -verbose -keystore ~/android-release-key.keystore $TEMPPGF phrasedroid
mv $TEMPPGF bin/PhraseDroid-signed.apk
jarsigner -verify bin/PhraseDroid-signed.apk
