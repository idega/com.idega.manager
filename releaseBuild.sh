#!/bin/sh
echo "[Helper] First step: Updating project and classpath? [y/n]"
read answer
if [ "$answer" = "y" ] || [ "$answer" = "Y" ]
then
maven eclipse
fi


echo "[Helper] Next step: Change snapshots in project.xml? (comments out SNAPSHOT) [y/n]"
read answer
if [ "$answer" = "y" ] || [ "$answer" = "Y" ]
then
cp project.xml project_helper.xml
sed 's/<version>SNAPSHOT<\/version>/<!--HELPERversion>SNAPSHOT<\/versionHELPER-->/g;s/<!--version>/<!--HELPER--><version>/g;s/<\/version-->/<\/version><!--HELPER-->/g' project_helper.xml > project.xml
rm project_helper.xml
fi


echo "[Helper] Next step: Clean target? [y/n]"
read answer
if [ "$answer" = "y" ] || [ "$answer" = "Y" ]
then
maven clean
fi

echo "[Helper] Next step: Test and Compile? [y/n]"
read answer
if [ "$answer" = "y" ] || [ "$answer" = "Y" ]
then
maven java:compile
fi


echo "[Helper] Before this step now changed project.xml, changes.xml, project and classpath. Next step: Prepare Release? [y/n]"
read answer
if [ "$answer" = "y" ] || [ "$answer" = "Y" ]
then
maven scm:prepare-release
fi


echo "[Helper] Next step: Deploy built jar/iwbar to central repository? [y/n]?"
read answer
if [ "$answer" = "y" ] || [ "$answer" = "Y" ]
then
maven iw-bundle:deploy
fi


echo "[Helper] Next step: Change snapshots in project.xml? (comments in SNAPSHOT) [y/n]?"
read answer
if [ "$answer" = "y" ] || [ "$answer" = "Y" ]
then
cp project.xml project_helper.xml
sed 's/<!--HELPERversion>SNAPSHOT<\/versionHELPER-->/<version>SNAPSHOT<\/version>/g;s/<!--HELPER--><version>/<!--version>/g;s/<\/version><!--HELPER-->/<\/version-->/g' project_helper.xml > project.xml
rm project_helper.xml
echo "[Helper]...project.xml was modified! Change project.xml. Commit project.xml!"
fi



