# PDF Generator (HTML/Markdown -> PDF con portada)

Build:
- mvn -q -DskipTests -f tools/pdfgen/pom.xml package

Uso:
- java -jar tools/pdfgen/target/pdfgen-jar-with-dependencies.jar --title "Mi Libro" --author "Autor X" --cover "c:\ruta\portada.png" --input "c:\ruta\contenido.md" --out "c:\ruta\salida.pdf"

Notas:
- Soporta .md o .html en --input
- Puedes pasar --css "ruta.css" para estilos personalizados
- Coloca fuentes TTF opcionales en resources/fonts/ y registra más en PdfGenerator si quieres
