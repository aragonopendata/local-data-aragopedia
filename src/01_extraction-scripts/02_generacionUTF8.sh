mkdir ../../data/resource/DatosDescarga-UTF8
cd ../../data/resource/DatosDescarga
for f in `find . -name "*.csv"`; do
  echo $f
  iconv -f ISO-8859-1 -t UTF-8 $f | sed "s/ÿþ//g" > ../DatosDescarga-UTF8/$f
done
cd ../../../src/01_extraction-scripts


