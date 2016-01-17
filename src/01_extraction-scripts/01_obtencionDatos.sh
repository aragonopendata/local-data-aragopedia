mkdir ../../data/resource/DatosDescarga
#for linea in `cat InformesEstadisticaLocal-URLs.csv`; do
for linea in `sort -u ../00_select-reports/InformesEstadisticaLocal-URLs.csv | cat`; do  #eliminamos duplicados
  IFS=',' read -ra valores <<< "$linea"
  f=../../data/resource/DatosDescarga/${valores[1]}.csv
  curl -v -L -A "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36" --cookie "sawU=granpublico; ORA_BIPS_LBINFO=15232b39ab3; ORA_BIPS_NQID=cgfcmddr1jc91il5rs6v7rrvh9mfk4abi55t3u32suo1jhgizOr07UFe9WiFvM3; __utma=263932892.849551431.1443517596.1449972362.1450114786.7; __utmz=263932892.1444514259.3.3.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)" "http://bi.aragon.es/analytics/saw.dll?Go&path=/shared/IAEST-PUBLICA/Estadistica%20Local/${valores[0]}&Action=Download&Options=df&NQUser=granpublico&NQPassword=granpublico" -o $f
  sleep 10
done

#segunda pasada para comprobar cuáles han dado algún tipo de error
for linea in `sort -u ../00_select-reports/InformesEstadisticaLocal-URLs.csv | cat`; do  #eliminamos duplicados
  IFS=',' read -ra valores <<< "$linea"
  f=../../data/resource/DatosDescarga/${valores[1]}.csv
  if grep -q "<!DOCTYPE HTML" $f; then
    curl -v -L -A "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36" --cookie "sawU=granpublico; ORA_BIPS_LBINFO=15232b39ab3; ORA_BIPS_NQID=cgfcmddr1jc91il5rs6v7rrvh9mfk4abi55t3u32suo1jhgizOr07UFe9WiFvM3; __utma=263932892.849551431.1443517596.1449972362.1450114786.7; __utmz=263932892.1444514259.3.3.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)" "http://bi.aragon.es/analytics/saw.dll?Go&path=/shared/IAEST-PUBLICA/Estadistica%20Local/${valores[0]}&Action=Download&Options=df&NQUser=granpublico&NQPassword=granpublico" -o $f
  fi
  if grep -q "<HTML>" $f; then
    curl -v -L -A "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36" --cookie "sawU=granpublico; ORA_BIPS_LBINFO=15232b39ab3; ORA_BIPS_NQID=cgfcmddr1jc91il5rs6v7rrvh9mfk4abi55t3u32suo1jhgizOr07UFe9WiFvM3; __utma=263932892.849551431.1443517596.1449972362.1450114786.7; __utmz=263932892.1444514259.3.3.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)" "http://bi.aragon.es/analytics/saw.dll?Go&path=/shared/IAEST-PUBLICA/Estadistica%20Local/${valores[0]}&Action=Download&Options=df&NQUser=granpublico&NQPassword=granpublico" -o $f
  fi
done

#nota: aún puede quedar algún fichero erróneo