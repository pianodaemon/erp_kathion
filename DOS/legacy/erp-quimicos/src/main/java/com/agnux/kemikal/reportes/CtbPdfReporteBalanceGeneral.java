package com.agnux.kemikal.reportes;

import com.agnux.common.helpers.StringHelper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CtbPdfReporteBalanceGeneral {
    public File archivoSalida;
    HashMap<String, String> datosHeaderFooter;
    HashMap<String, String> datosEmp;
    public List<HashMap<String, String>> rows;
    
    public File getArchivoSalida() {
        return archivoSalida;
    }

    public void setArchivoSalida(File archivoSalida) {
        this.archivoSalida = archivoSalida;
    }
    
    public HashMap<String, String> getDatosEmp() {
        return datosEmp;
    }

    public void setDatosEmp(HashMap<String, String> datosEmp) {
        this.datosEmp = datosEmp;
    }
    
    public HashMap<String, String> getDatosHeaderFooter() {
        return datosHeaderFooter;
    }

    public void setDatosHeaderFooter(HashMap<String, String> datosHeaderFooter) {
        this.datosHeaderFooter = datosHeaderFooter;
    }

    public List<HashMap<String, String>> getRows() {
        return rows;
    }
    
    public void setRows(List<HashMap<String, String>> rows) {
        this.rows = rows;
    }
    
    public CtbPdfReporteBalanceGeneral(String fileout, HashMap<String, String> datosEncabezadoPie, HashMap<String, String> datosEmpresa, ArrayList<HashMap<String, String>> itemsForPrinting){
        this.setArchivoSalida(new File(fileout));
        this.setDatosHeaderFooter(datosEncabezadoPie);
        this.setDatosEmp(datosEmpresa);
        this.setRows(itemsForPrinting);
        this.init();
    }
    
    private void init(){
        Font smallsmall = new Font(Font.getFamily("ARIAL"),13,Font.NORMAL);
        Font smallBoldFontWhite = new Font(Font.getFamily("ARIAL"),9,Font.BOLD,BaseColor.WHITE);
        Font smallFontBlack = new Font(Font.getFamily("ARIAL"),9,Font.NORMAL,BaseColor.BLACK);
        
        float [] ancho_columnas = {7f,1.5f,1.5f};
        Font smallFontBold = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
        
        HeaderFooter event = new HeaderFooter(this.getDatosHeaderFooter());
        Document doc = new Document(PageSize.LETTER,-50,-50,60,30);
        doc.addCreator("replicas@gmail.com");
        doc.addAuthor("Six Million dollar weirdo");
        PdfWriter writer = null;
        try {
            
            writer = PdfWriter.getInstance(doc, new FileOutputStream(this.getArchivoSalida()));
            writer.setPageEvent(event);
            doc.open();
            
            //Se declara la tabla y se establecen la configuraciones para la misma
            PdfPTable table = new PdfPTable(ancho_columnas);
            table.setKeepTogether(false);
            table.setHeaderRows(3);
            PdfPCell cellHead=null;
            
            //Fila 1
            cellHead = new PdfPCell(new Paragraph(this.getDatosEmp().get("emp_calle").toUpperCase()+" "+this.getDatosEmp().get("emp_no_exterior")+", "+this.getDatosEmp().get("emp_colonia"),smallFontBlack));
            cellHead.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellHead.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellHead.setUseAscender(true);
            cellHead.setUseDescender(true);
            cellHead.setFixedHeight(11);
            cellHead.setBorder(0);
            table.addCell(cellHead);
            
            cellHead = new PdfPCell(new Paragraph("        RFC:          "+this.getDatosEmp().get("emp_rfc").toUpperCase(),smallFontBlack));
            cellHead.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellHead.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellHead.setUseAscender(true);
            cellHead.setUseDescender(true);
            cellHead.setFixedHeight(11);
            cellHead.setBorder(0);
            cellHead.setColspan(2);
            table.addCell(cellHead);
            
            //Fila 2
            cellHead = new PdfPCell(new Paragraph(this.getDatosEmp().get("emp_municipio").toUpperCase()+", "+this.getDatosEmp().get("emp_estado").toUpperCase()+", "+this.getDatosEmp().get("emp_pais").toUpperCase()+", C.P."+this.getDatosEmp().get("emp_cp"),smallFontBlack));
            cellHead.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellHead.setVerticalAlignment(Element.ALIGN_TOP);
            cellHead.setUseAscender(true);
            cellHead.setUseDescender(true);
            cellHead.setFixedHeight(13);
            cellHead.setBorder(0);
            table.addCell(cellHead);
            
            cellHead = new PdfPCell(new Paragraph("        Impreso:    "+this.getDatosHeaderFooter().get("fecha_impresion"),smallFontBlack));
            cellHead.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellHead.setVerticalAlignment(Element.ALIGN_TOP);
            cellHead.setUseAscender(true);
            cellHead.setUseDescender(true);
            cellHead.setFixedHeight(13);
            cellHead.setBorder(0);
            cellHead.setColspan(2);
            table.addCell(cellHead);
            
            String[] columnasHeader = {"Concepto",""};
            List<String>  lista_columnas_header = (List<String>) Arrays.asList(columnasHeader);
            for ( String columna_titulo : lista_columnas_header){
                PdfPCell cellX = new PdfPCell(new Paragraph(columna_titulo,smallBoldFontWhite));
                cellX.setUseAscender(true);
                cellX.setUseDescender(true);
                cellX.setBackgroundColor(BaseColor.BLACK);
                
                if (columna_titulo.equals("Concepto")){
                    cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                }
                
                if (columna_titulo.equals("")){
                    cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellX.setColspan(2);
                }
                
                cellX.setFixedHeight(13);
                table.addCell(cellX);
            }
            
            
            
            // Pintar los rows del Reporte
            for (HashMap<String, String> i : this.getRows()){
                //Indices del HashMap que representa el row
                String[] wordList = {"descripcion","saldo_fin",};
                
                List<String>  indices = (List<String>) Arrays.asList(wordList);
                String valor="";
                
                for (String omega : indices){
                    PdfPCell celda = null;
                    
                    if (omega.equals("descripcion")){
                        if (!i.get("tipo_reg").equals("3")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFontBold));
                        }else{
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                        }
                        
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    }
                    
                    if (omega.equals("saldo_fin")){
                        if (!i.get("tipo_reg").equals("3")){
                            celda = new PdfPCell(new Paragraph((i.get(omega).trim().equals("")? "":StringHelper.AgregaComas(i.get(omega))),smallFontBold));
                        }else{
                            celda = new PdfPCell(new Paragraph((i.get(omega).trim().equals("")? "":StringHelper.AgregaComas(i.get(omega))),smallFont));
                        }
                        
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        celda.setColspan(2);
                    }
                    

                    
                    
                    celda.setBorder(0);
                    table.addCell(celda);
                }  
            }
            
            doc.add(table);   
            doc.close();
        } catch (Exception ex) {
            Logger.getLogger(CtbPdfReporteBalanceGeneral.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    
    static class HeaderFooter extends PdfPageEventHelper {
        protected PdfTemplate total;       
        protected BaseFont helv;  
        protected PdfContentByte cb;  
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        Font largeFont = new Font(Font.FontFamily.HELVETICA,10,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        
        //ESTAS SON VARIABLES PRIVADAS DE LA CLASE, SE LE ASIGNA VALOR EN EL CONSTRUCTOR SON SETER
        private String empresa;
        private String periodo;
        private String titulo_reporte;
        private String codigo1;
        private String codigo2;
        
        //ESTOS  SON LOS GETER Y SETTER DE LAS VARIABLES PRIVADAS DE LA CLASE
        public String getCodigo1() {
            return codigo1;
        }
        
        public void setCodigo1(String codigo1) {
            this.codigo1 = codigo1;
        }

        public String getCodigo2() {
            return codigo2;
        }

        public void setCodigo2(String codigo2) {
            this.codigo2 = codigo2;
        }

        public String getTitulo_reporte() {
            return titulo_reporte;
        }

        public void setTitulo_reporte(String titulo_reporte) {
            this.titulo_reporte = titulo_reporte;
        }
        
        public String getEmpresa() {
            return empresa;
        }
        
        public void setEmpresa(String empresa) {
            this.empresa = empresa;
        }

        public String getPeriodo() {
            return periodo;
        }

        public void setPeriodo(String periodo) {
            this.periodo = periodo;
        }
        
        //ESTE ES EL CONSTRUCTOR DE LA CLASE  QUE RECIBE LOS PARAMETROS
        HeaderFooter( HashMap<String, String> datos ){
            this.setEmpresa(datos.get("empresa"));
            this.setTitulo_reporte(datos.get("titulo_reporte"));
            this.setPeriodo(datos.get("periodo"));
            this.setCodigo1(datos.get("codigo1"));
            this.setCodigo2(datos.get("codigo2"));
        }
        
        
        /*Añadimos una tabla con  una imagen del logo de megestiono y creamos la fuente para el documento, la imagen esta escalada para que no se muestre pixelada*/   
        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            try {
                total = writer.getDirectContent().createTemplate(100, 100);  
                //public Rectangle(int x, int y, int width, int height)
                total.setBoundingBox(new Rectangle(-20, -20, 100, 100));
                total.fill();
                helv = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);
            }
            catch(Exception e) {
                throw new ExceptionConverter(e);
            }
        }
        
        /*añadimos pie de página, borde y más propiedades*/
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getEmpresa(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop() -25, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getTitulo_reporte(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getPeriodo(),largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
            cb = writer.getDirectContent();
            float textBase = document.bottom() - 20;
            
            //texto inferior izquieda pie de pagina
            String text_left = this.getCodigo1();
            float text_left_Size = helv.getWidthPoint(text_left, 7);
            cb.beginText();
            cb.setFontAndSize(helv, 7);  
            cb.setTextMatrix(document.left()+85, textBase );  //definir la posicion de text
            cb.showText(text_left);
            cb.endText();
            
            
            //texto centro pie de pagina
            String text_center ="Página " + writer.getPageNumber() + " de ";
            float text_center_Size = helv.getWidthPoint(text_center, 7);
            float pos_text_center = (document.getPageSize().getWidth()/2)-(text_center_Size/2);
            float adjust = text_center_Size + 3; 
            cb.beginText();  
            cb.setFontAndSize(helv, 7);  
            cb.setTextMatrix(pos_text_center, textBase );  //definir la posicion de text
            cb.showText(text_center);
            cb.endText();
            cb.addTemplate(total, pos_text_center + adjust, textBase);
            
            
            //texto inferior derecha pie de pagina
            String text_right = this.getCodigo2();
            float textRightSize = helv.getWidthPoint(text_right, 7);
            float pos_text_right = document.getPageSize().getWidth()-textRightSize - 40;
            cb.beginText();
            cb.setFontAndSize(helv, 7);
            cb.setTextMatrix(pos_text_right, textBase);
            cb.showText(text_right);
            cb.endText();
            //cb.restoreState();  
        }
        
        /*aqui escrimos ls paginas totales, para que nos salga de pie de pagina Pagina x de y*/
        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
          total.beginText();  
          total.setFontAndSize(helv, 7);  
          total.setTextMatrix(0,0);                                           
          total.showText(String.valueOf(writer.getPageNumber() -1));  
          total.endText();  
        }
   };//termina clase HeaderFooter
    
    
}
