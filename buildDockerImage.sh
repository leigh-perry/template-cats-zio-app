#!/bin/bash
PACK_FOLDER=target/pack
APP_FOLDER=appjars

cleanup() {
  rm -rf $PACK_FOLDER/$APP_FOLDER
}

createTempFolder() {
  cleanup
  mkdir $PACK_FOLDER/$APP_FOLDER
}

main() {
  # Move second layer files to separate folder
  createTempFolder
  mv $PACK_FOLDER/lib/*lptemplateprojectname* $PACK_FOLDER/$APP_FOLDER

  docker build -t lptemplatecompany/lptemplatedivision-lptemplateprojectname:latest .

  # Restore to built image
  mv $PACK_FOLDER/$APP_FOLDER/* $PACK_FOLDER/lib/
  cleanup
}

main "$@"
