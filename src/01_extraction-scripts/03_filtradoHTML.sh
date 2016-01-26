cd ../../data/resource/DatosDescarga-UTF8

#Eliminar los ficheros que han devuelto HTML en lugar de CSVs
#Dos tipos de casos identificados: aquellos en los que Oracle BI vuelve a pedir usuario y password, y que comienzan por <!DOCTYPE HTML, y aquellos en los que se dice página en mantenimiento, que empiezan por <HTML>
mkdir ../InformesErroresHTML
for f in `ls *.csv`; do
 echo "Checking $f"
 if grep -q "<!DOCTYPE HTML" $f; then
  mv $f ../InformesErroresHTML
 fi
 if grep -q "<DOCTYPE HTML>" $f; then
  mv $f ../InformesErroresHTML
 fi
 if grep -q "<div>" $f; then
  mv $f ../InformesErroresHTML
 fi
 if grep -q "<HTML>" $f; then
  mv $f ../InformesErroresHTML
 fi
 #tratando también con los ficheros binarios que a veces devuelve OracleBI
 if tr '[\000-\011\013-\037\177-\377]' '.' < $f | grep -q 'D.O.C.T.Y.P.E'; then
  mv $f ../InformesErroresHTML
 fi
 if tr '[\000-\011\013-\037\177-\377]' '.' < $f | grep -q '<.d.i.v'; then
  mv $f ../InformesErroresHTML
 fi
done

ls ../InformesErroresHTML > ../InformesErroresHTML.txt

cd ../../src/01_extraction-scripts
pwd

