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
cp dist/MapExplorer.jar lib/*.map lib/*.properties README.txt License.txt lib/MapExplorer.bat lib/mapexplorer tmp/MapExplorer
cd tmp
unix2dos MapExplorer/*.bat
zip -9 -r MapExplorer-$version.zip MapExplorer

# source
cd ..
zip -9 tmp/MapExplorer-$version.src.zip `find . -name '*.java' -print` build.xml License.txt \
 lib/*.map README.txt lib/*.properties lib/mapexplorer lib/MapExplorer.bat

cp www/* tmp/MapExplorer-$version.*zip ~/prj/ddm/mapexplorer
