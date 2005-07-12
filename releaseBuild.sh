#!/bin/sh
echo "[Helper] First step: Updating project and classpath? [y/n]"
read answer
if [ "$answer" = "n" ] || [ "$answer" = "N" ]
then
echo "[Helper] Aborting"
exit
fi
maven eclipse

echo "[Helper] ... project and classpath were updated. Next step: Preparing project.xml? (comments out SNAPSHOT) [y/n]"
read answer
if [ "$answer" = "n" ] || [ "$answer" = "N" ]
then
echo "[Helper] Aborting"
exit
fi
cp project.xml project_helper.xml
sed 's/<version>SNAPSHOT<\/version>/<!--HELPERversion>SNAPSHOT<\/versionHELPER-->/g;s/<!--version>/<!--HELPER--><version>/g;s/<\/version-->/<\/version><!--HELPER-->/g' project_helper.xml > project.xml
rm project_helper.xml

echo "[Helper]...project.xml was modified! Change project.xml and change.xml. Next step: Cleaning target? [y/n]"
read answer
if [ "$answer" = "n" ] || [ "$answer" = "N" ]
then
echo "[Helper] Aborting"
exit
fi
maven clean

echo "[Helper] ... target was cleaned. Next step: Testing compiling? [y/n]"
read answer
if [ "$answer" = "n" ] || [ "$answer" = "N" ]
then
echo "[Helper] Aborting"
exit
fi
maven java:compile

echo "[Helper] ...testing compiling finished. Commit now changed project.xml , changes.xml, project and classpath. Next step: Preparing release? [y/n]"
read answer
if [ "$answer" = "n" ] || [ "$answer" = "N" ]
then
echo "[Helper] Aborting"
exit
fi
maven scm:prepare-release

echo "[Helper] ... preparing release finished. Next step: Deploying iwbar? [y/n]?"
read answer
if [ "$answer" = "n" ] || [ "$answer" = "N" ]
then
echo "[Helper] Aborting"
exit
fi
maven iw-bundle:deploy

echo "[Helper] ... deploying iwbar finished. Next step: Preparing project.xml? (comments in SNAPSHOT) [y/n]?"
read answer
if [ "$answer" = "n" ] || [ "$answer" = "N" ]
then
echo "[Helper] Aborting"
exit
fi
cp project.xml project_helper.xml
sed 's/<!--HELPERversion>SNAPSHOT<\/versionHELPER-->/<version>SNAPSHOT<\/version>/g;s/<!--HELPER--><version>/<!--version>/g;s/<\/version><!--HELPER-->/<\/version-->/g' project_helper.xml > project.xml
rm project_helper.xml

echo "[Helper]...project.xml was modified! Change project.xml. Commit project.xml!"
