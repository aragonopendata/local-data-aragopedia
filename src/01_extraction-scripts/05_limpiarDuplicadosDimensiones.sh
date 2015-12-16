mkdir ../../data/resource/DimensionesFiltradas

cd ../../data/resource/DatosPorColumnas

#NUMDIMENSIONES=`ls -d */|wc -l`
#echo $NUMDIMENSIONES
ls -d */ > ../dimensionesPaso4.txt

IFS=$'\n'
for d in `cat ../dimensionesPaso4.txt`; do
 echo "${d}"
 cd "${d}"
 echo "Inside the directory $d"
 pwd


 #rm filtered.txt
 NOMBREFICHERO=`echo ${d:0:${#d}-1}`
 echo $NOMBREFICHERO

 for f in `ls`; do
  echo "${f}"
  sed '1d' "${f}" >> ../../DimensionesFiltradas/"${NOMBREFICHERO}".txt
 done
 sort -u ../../DimensionesFiltradas/"${NOMBREFICHERO}".txt > ../../DimensionesFiltradas/"${NOMBREFICHERO}"_temp.txt
 sed '/^$/d' ../../DimensionesFiltradas/"${NOMBREFICHERO}"_temp.txt > ../../DimensionesFiltradas/"${NOMBREFICHERO}".txt
 rm ../../DimensionesFiltradas/"${NOMBREFICHERO}"_temp.txt
 cd ..
done

cd ..
rm dimensionesPaso4.txt

cd ../src/01_extraction-scripts


