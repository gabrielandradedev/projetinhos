echo "Limpando o output..."
if [ -d out ]; then rm -Rf out; fi

echo "Listando as sources..."
SOURCES=$(find . -name "*.java")

echo "Compilando o programa..."
javac $SOURCES -d out

echo "Rodando o programa..."
cd out || echo "Houve algum problema durante a compilação!" > log.txt
java application/Program
