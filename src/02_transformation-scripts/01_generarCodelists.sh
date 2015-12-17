mkdir ../../data/dump/DatosTTL
mkdir ../../data/dump/DatosTTL/codelists

#cd ../../data/resource/DimensionesCuradas/codelists

pwd

IFS=$'\n'

for f in `ls ../../data/resource/DimensionesCuradas/codelists`; do
 echo "${f}"
 nombreExtension=`basename "$f"`
 nombre=`echo $nombreExtension | sed -e 's/.txt//'`
 fichero=../../data/dump/DatosTTL/codelists/$nombre.ttl
 touch "$fichero"
 echo "@prefix rdfs:    <http://www.w3.org/2000/01/rdf-schema#> ." >> $fichero
 echo "@prefix foaf:    <http://xmls.com/foaf/0.1/> ." >> $fichero
 echo "@prefix owl:     <http://www.w3.org/2002/07/owl#> ." >> $fichero
 echo "@prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ." >> $fichero
 echo "@prefix skos:    <http://www.w3.org/2004/02/skos/core#> ." >> $fichero
 echo "@prefix dcterms:  <http://purl.org/dc/terms/> ." >> $fichero

#conceptscheme=`head -1 $f | tr '[:space:]()%=><-.' '-'`
#conceptscheme=`echo $nombre | sed -e 's/.txt//' -e 's/[[:space:]]*$//g' -e 's/[[:space:]]/-/g' -e 's/āáǎàēéěèīíǐìōóǒòūúǔùǖǘǚǜĀÁǍÀĒÉĚÈĪÍǏÌŌÓǑÒŪÚǓÙǕǗǙǛ/aaaaeeeeiiiioooouuuuüüüüAAAAEEEEIIIIOOOOUUUUÜÜÜÜ/' | tr '[:upper:]' '[:lower:]'`
conceptscheme=`echo $nombre | sed -e 's/[[:space:]]*$//g' | sed -e 's/[[:space:]]/-/g' | tr '[:upper:]' '[:lower:]' | sed -e 's/[à,â,á]/a/g;s/[õó]/o/g;s/[íì]/i/g;s/[êệé]/e/g' | tr 'āáǎàēéěèīíǐìōóǒòūúǔùǖǘǚǜĀÁǍÀĒÉĚÈĪÍǏÌŌÓǑÒŪÚǓÙǕǗǙǛ' 'aaaaeeeeiiiioooouuuuüüüüAAAAEEEEIIIIOOOOUUUUÜÜÜÜ'`
#conceptscheme2=`echo $conceptscheme | iconv -f utf8 -t ascii//TRANSLIT`
#| tr '[:space:]()%=><-.' '-'`
 echo "<http://opendata.aragon.es/kos/iaest/$conceptscheme> a skos:ConceptScheme ;" >> $fichero
 echo " skos:notation \"$conceptscheme\" ;" >> $fichero
 echo " rdfs:label \"$nombre\"@es ." >> $fichero
 for valor in `cat ../../data/resource/DimensionesCuradas/codelists/${f}`; do
  valorCoded=`echo $valor | sed -e 's/[[:space:]]*$//g' | sed -e 's/[[:space:]]/-/g' | tr '[:upper:]' '[:lower:]' | sed -e 's/[à,â,á]/a/g;s/[õó]/o/g;s/[íì]/i/g;s/[êệé]/e/g' | tr 'āáǎàēéěèīíǐìōóǒòūúǔùǖǘǚǜĀÁǍÀĒÉĚÈĪÍǏÌŌÓǑÒŪÚǓÙǕǗǙǛ' 'aaaaeeeeiiiioooouuuuüüüüAAAAEEEEIIIIOOOOUUUUÜÜÜÜ'`
  echo "" >> $fichero
  echo "<http://opendata.aragon.es/kos/iaest/$conceptscheme/$valorCoded> a skos:Concept ;" >> $fichero
  echo "  skos:inScheme <http://opendata.aragon.es/kos/iaest/$conceptscheme> ;" >> $fichero
  echo "  skos:notation \"$valorCoded\" ;" >> $fichero
  echo "  skos:prefLabel \"$valor\" ." >> $fichero
 done
done

cd ../../src/02_transformation-scripts



#<http://eurostat.linked-statistics.org/dic/sex#M>
#a       skos:Concept ;
#skos:inScheme <http://eurostat.linked-statistics.org/dic/sex> ;
#skos:notation "M" ;
#skos:prefLabel "Males"@en .