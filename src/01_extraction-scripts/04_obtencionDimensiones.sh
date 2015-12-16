cd ../../data/resource/DatosDescarga-UTF8


head -n 1 *.csv >> ../heads_temp.txt
#sed "s/ÿþ//g" ../heads_temp.txt > ../heads_temp2.txt
TAB=$'\t'
RETURN=$'\r'
sed "s/${TAB}/${RETURN}/g" ../heads_temp.txt > ../heads_Mac.txt
tr '\r' '\n' < ../heads_Mac.txt > ../heads_Unix.txt
tr -cd '\11\12\15\40-\176áÁéÉíÍóÓüúÚÜñÑ%' < ../heads_Unix.txt > ../heads_UnixClean.txt
tr -dc '[:alnum:][:space:]()%=><-.\n\r' < ../heads_UnixClean.txt > ../heads_UnixCleanClean.txt
sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//' < ../heads_UnixCleanClean.txt > ../heads.txt
rm ../heads_temp.txt
rm ../heads_Mac.txt
rm ../heads_Unix.txt
rm ../heads_UnixClean.txt
rm ../heads_UnixCleanClean.txt


#for line in `cat ../heads_UnixCleanClean.txt`; do
 #echo "$line" | xargs >> ../heads_temp.txt
 #echo ${line}
 #NEWDIMENSION=$(echo -e "${line}" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')
 #echo $NEWDIMENSION >> ../heads_temp.txt
#done
#rm ../heads_Unix.txt
#mv ../heads_temp.txt ../heads.txt


mkdir ../DatosPorColumnas
for f in `find . -name "*.csv"`; do
  FBASENAME=`basename "$f"`

  NUMCOLUMNS=`head -n 1 $f | tr $'\t' $'\n' | wc -l`

  while [ $NUMCOLUMNS -gt 0 ]; do
    cut -f $NUMCOLUMNS $f | tr -cd '\11\12\15\40-\176áÁéÉíÍóÓüúÚÜñÑ%' | tr -dc '[:alnum:][:space:]()%=><-.\n\r' | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//' > ../DatosPorColumnas/temp.csv
    DIMENSION=`head -1 ../DatosPorColumnas/temp.csv`
    #echo $DIMENSION
    DIMENSION_CLEAN=`echo "$DIMENSION" | tr -cd '\11\12\15\40-\176áÁéÉíÍóÓüúÚÜñÑ%' | tr -dc '[:alnum:][:space:]()%=><-.\n\r' | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//'`
    mkdir "../DatosPorColumnas/${DIMENSION_CLEAN}"
    mv ../DatosPorColumnas/temp.csv "../DatosPorColumnas/${DIMENSION_CLEAN}/$FBASENAME"
    let NUMCOLUMNS=NUMCOLUMNS-1
  done


done



#ENCONTRAR ERRORES EN LAS DIMENSIONES
cd ../DatosPorColumnas
ls -p | grep -v / > ../InformesConDatosSinDimension.txt
mkdir ../InformesConDatosSinDimension
for f in `ls -p | grep -v /`; do
 mv $f ../InformesConDatosSinDimension
done



cd ../../src/01_extraction-scripts


#  echo $f $NUMCOLUMNS
# head -n 1 $f >> ../heads.txt
#  echo -e "\n" >> ../heads.txt

#  sed "s/${TAB}/${RETURN}/g" ../temp.txt > ../temp2.txt
#  grep "${TAB}" ../temp.txt > ../temp3.txt

#  cat ../temp2.txt
#  NUMCOLUMNS=`wc -l ../temp2.txt`
#  ECHO $NUMCOLUMNS

#COLUMNS=`head -n 1 $f`
#  echo $COLUMNS
#  COLUMNS=`head -n 1 $f | sed "s/$\{TAB}/${RETURN}/g"`
#  echo $COLUMNS
#  NUMCOLUMNS=`head -n 1 $f | sed 's/[^\${TAB}]//g' | wc -c`
#  echo $NUMCOLUMNS
#  NUMCOLUMNS=`head -n º51 $f | grep -o "${TAB}" | wc -l`
#  echo $NUMCOLUMNS
