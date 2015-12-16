cd ../../data/resource/DimensionesFiltradas

mkdir ../DimensionesFinales
mkdir ../DimensionesFinales/PosiblesDimensionesMenos20Valores #Para los que probablemente se generen codelists
mkdir ../DimensionesFinales/Undecided #Para los que pueden ser dimensiones o measures
mkdir ../DimensionesFinales/PosiblesMeasures  #Para las dimensiones con más de 50 valores

IFS=$'\n'

for f in `ls`; do
 echo "${f}"
 NUMVALORESDISTINTOS=`cat "${f}" | wc -l | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//'`
 echo $NUMVALORESDISTINTOS
 if [ $NUMVALORESDISTINTOS -le 20 ]
 then cp "${f}" ../DimensionesFinales/PosiblesDimensionesMenos20Valores
 elif [ $NUMVALORESDISTINTOS -le 50 ]
 then cp "${f}" ../DimensionesFinales/Undecided
 else cp "${f}" ../DimensionesFinales/PosiblesMeasures
 fi
done

cd ../../src/01_extraction-scripts
