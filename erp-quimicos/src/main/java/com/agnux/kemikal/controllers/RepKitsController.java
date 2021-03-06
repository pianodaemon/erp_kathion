/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

/*
  @author valentin santos s.
 */
import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.InvInterfaceDao;
import com.agnux.kemikal.reportes.PdfRepKits;
import com.itextpdf.text.DocumentException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author valentin.vale8490@gmail.com
 * 02/abril/2012
 * Este controller es para para las vistas de los pdf que muestran el pronostico de cobranza por semana 
 */

@Controller
@SessionAttributes({"user"})
@RequestMapping("/repinvkits/")
public class RepKitsController {
    private static final Logger log  = Logger.getLogger(RepKitsController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoInv")
    private InvInterfaceDao invDao;

    public InvInterfaceDao getInvDao() {
        return invDao;
    }

    public void setInvDao(InvInterfaceDao invDao) {
        this.invDao = invDao;
    }

    public ResourceProject getResource() {
        return resource;
    }
    
    public void setResource(ResourceProject resource) {
        this.resource = resource;
    }
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
    @ModelAttribute("user") UserSessionData user)
    throws ServletException, IOException {
        log.log(Level.INFO, "Ejecutando starUp de {0}", RepKitsController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        ModelAndView x = new ModelAndView("repinvkits/startup", "title", "Reporte de Existencias en Kits");
        
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
        x = x.addObject("url", resource.getUrl(request));
        x = x.addObject("username", user.getUserName());
        x = x.addObject("empresa", user.getRazonSocialEmpresa());
        x = x.addObject("sucursal", user.getSucursal());
        
        String userId = String.valueOf(user.getUserId());
        
        String codificado = Base64Coder.encodeString(userId);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
        
        return x;
    }
    
    
    //obtiene datos 
    @RequestMapping(value="/getKitsVista.json", method = RequestMethod.POST)
    public @ResponseBody ArrayList<HashMap<String, String>> getKitsVistaJson(
            @RequestParam(value="codigo", required=true) String codigo,
            @RequestParam(value="descripcion", required=true) String descripcion,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        log.log(Level.INFO, "Ejecutando getKitsVista de {0}", RepKitsController.class.getName());
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        ArrayList<HashMap<String, String>> z = this.getInvDao().getKits(id_empresa,id_usuario,codigo,descripcion);
        return z;
        
    }
    
    
     //reporte Pronostico de Cobranza  "/getPronosticoDeCobranza/{opcion_seleccionada}/{numero_semanas}/{iu}/out.json"
     //http://localhost:8080/erp-quimicos/controllers/reppronoscobranza/getPronosticoDeCobranza/0/4/NA==/out.json
     //@RequestMapping(value = "/PDFgetKits{cod}/{desc}/{iu}/out.json", method = RequestMethod.GET ) 
     @RequestMapping(value = "/PDFgetKits/{iu}/out.json", method = RequestMethod.GET ) 
     public ModelAndView PdfVentasNetasxCliente(
//                @PathVariable("cod") String codigo,
//                @PathVariable("desc") String descripcion,
                @PathVariable("iu") String id_user_cod,
                HttpServletRequest request,
                HttpServletResponse response, 
                Model model)
     throws ServletException, IOException, URISyntaxException, DocumentException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        System.out.println("Generando reporte de Kits que se pueden formar con de acuerdo  a la existencia");
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        
        String[] array_company = razon_social_empresa.split(" ");
        String company_name= array_company[0].toLowerCase();
        //String ruta_imagen = this.getPgdao().getImagesDir() +"logo_"+ company_name +".png";
        
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        
        String file_name = "exis_kits.pdf";
        
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        //String fileout ="C:\\Users\\micompu\\Desktop\\Kits que se pueden realizar.pdf";
        
        ArrayList<HashMap<String, String>> lista_kits = new ArrayList<HashMap<String, String>>();
        
        //obtiene los depositos del periodo indicado
       String codigo="";
       String descripcion="";
        lista_kits = this.getInvDao().getKits( id_empresa,id_usuario,codigo,descripcion); //llama al dao
        
        
        //instancia a la clase que construye el pdf de la del reporte de estado de cuentas del cliente
        PdfRepKits x = new PdfRepKits(lista_kits,razon_social_empresa,fileout);
        
        
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        int size = (int) file.length(); // Tamaño del archivo
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        response.setBufferSize(size);
        response.setContentLength(size);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getCanonicalPath() +"\"");
        FileCopyUtils.copy(bis, response.getOutputStream());          
        response.flushBuffer();
        
        return null;
        
    }
}
