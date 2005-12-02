#!/bin/sh
echo "Have you bumped the version number?"
echo "And committed?"
echo "And done cvs tag ?"
read answer
if [ ! $answer = yes ]; then
    exit 0
fi

ant jar
version=`java -jar dist/MapExplorer.jar -version | awk '{print $4}'`
mkdir tmp/MapExplorer
cp dist/MapExplorer.jar lib/*.map lib/README.txt lib/MapExplorer.bat lib/mapexplorer tmp/MapExplorer
cd tmp
unix2dos MapExplorer/*.bat
zip -9 -r MapExplorer-$version.zip MapExplorer
cp ../www/* MapExplorer-$version.zip /home/breitko/prj/ddm/mapexplorer
