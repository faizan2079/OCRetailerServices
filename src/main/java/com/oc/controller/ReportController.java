/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.fill.JRTemplatePrintFrame;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleDocxExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsxExporterConfiguration;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.oc.db.controller.OCUtil;
import org.oc.db.controller.Utilities;
import org.oc.db.entity.UserInformation;
import com.oc.services.entity.ReportFormat;
import javax.ws.rs.core.Response;

/**
 *
 * @author Arsalaan
 */
public class ReportController {
    /**
     * Exports Report
     * @param templatePath
     * @param reportingTempPath
     * @param reportPrintName
     * @param lstParameters
     * @param loggedUser
     * @return
     * @throws JRException 
     */
    public static String export(String templatePath, String reportingTempPath, String reportPrintName, Map lstParameters, UserInformation loggedUser) throws JRException{
        Connection con = null;
        Utilities utilities = new Utilities();
        Session dbSession = null;
        dbSession = OCUtil.getSessionFactoryOCOperations().openSession();
        SessionImpl sessionI = (SessionImpl) dbSession;
        con = (Connection) sessionI.connection();
        try {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(templatePath));
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,lstParameters,con);
            reportPrintName = loggedUser.getUserId()+"_"+reportPrintName+"_"+utilities.s7.format(new Date())+".pdf";
            JasperExportManager.exportReportToPdfFile(jasperPrint, reportingTempPath+File.separator+reportPrintName);
            return reportPrintName;
        }finally{
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
    }
    
    /**
     * Export Report Format
     * @param templatePath
     * @param reportingTempPath
     * @param reportPrintName
     * @param lstParameters
     * @param loggedUser
     * @param format
     * @return
     * @throws JRException 
     */
    public static String export(String templatePath, String reportingTempPath, String reportPrintName, Map lstParameters, UserInformation loggedUser, ReportFormat format) throws JRException{
        Connection con = null;
        Utilities utilities = new Utilities();
        Session dbSession = null;
        dbSession = OCUtil.getSessionFactoryOCOperations().openSession();
        SessionImpl sessionI = (SessionImpl) dbSession;
        con = (Connection) sessionI.connection();
        try {
            switch(format){
                case EXCEL:
                    lstParameters.put("IS_IGNORE_PAGINATION", true);
                    break;
            }
            
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(templatePath));
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,lstParameters,con);
            reportPrintName = loggedUser.getUserId()+"_"+reportPrintName+"_"+utilities.s7.format(new Date())+format.getExtension();
            switch(format){
                case DOCUMENT:
                    JRDocxExporter exporter = new JRDocxExporter();
                    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));      
                    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(reportingTempPath+File.separator+reportPrintName));
                    exporter.exportReport();
                    break;
                case PDF:
                    JasperExportManager.exportReportToPdfFile(jasperPrint, reportingTempPath+File.separator+reportPrintName);
                    break;
                case EXCEL:
                    JRXlsxExporter exporterXLS = new JRXlsxExporter();
                    exporterXLS.setExporterInput(new SimpleExporterInput(jasperPrint));      
                    exporterXLS.setExporterOutput(new SimpleOutputStreamExporterOutput(reportingTempPath+File.separator+reportPrintName));
                    exporterXLS.exportReport();
                    break;
            }
            return reportPrintName;
        }finally{
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
    }

    private static JasperPrint removeBlankElements(JasperPrint jasperPrint){
        JRPrintPage page = jasperPrint.getPages().get(jasperPrint.getPages().size()-1);
        JRTemplatePrintFrame frame = (JRTemplatePrintFrame)page.getElements().get(page.getElements().size()-1);
        Integer lastElementRemoved = null;
        
        for(int i = frame.getElements().size()-1; i >= 0; i--){
            JRPrintElement element = frame.getElements().get(i);
             if(element instanceof JRTemplatePrintFrame){
                 JRTemplatePrintFrame frameElement = (JRTemplatePrintFrame)element;
                 if(frameElement.getElements().isEmpty()){
                     frame.getElements().remove(i);
                     lastElementRemoved = i;
                 }
             }
        }
        
        if(!Objects.isNull(lastElementRemoved)){
            for(int i = lastElementRemoved; i < frame.getElements().size(); i++){
                JRPrintElement element = frame.getElements().get(i);
                 if(element instanceof JRTemplatePrintFrame){
                     JRTemplatePrintFrame frameElement = (JRTemplatePrintFrame)element;
                     frameElement.setY(frameElement.getY()-20);
                 }
            }
        }
        return jasperPrint;
    }
    
//    public static String getReportTemplatePath(SystemReport report, CompanyProfile company, ApplicationConfiguration config){
//        String reportTemplatePath = config.getReportTemplateRootPath()+company.getId()+File.separator+report.getTemplateName();
//        if(!new File(reportTemplatePath).exists()){
//            reportTemplatePath = config.getReportTemplateRootPath()+report.getTemplateName();
//        }
//        return reportTemplatePath;
//    }
    
    private static JasperPrint prepareReport(String templatePath, Map lstParameters, ReportFormat format) throws JRException{
        Connection con = null;
        Session dbSession = null;
        dbSession = OCUtil.getSessionFactoryOCOperations().openSession();
        SessionImpl sessionI = (SessionImpl) dbSession;
        con = (Connection) sessionI.connection();
        try {
            switch(format){
                case EXCEL:
                    lstParameters.put("IS_IGNORE_PAGINATION", true);
                    break;
            }
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(templatePath));
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,lstParameters,con);
            return jasperPrint;
        }finally{
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public static void export(String templatePath, String reportPrintName, Map lstParameters, UserInformation loggedUser, ReportFormat format, HttpServletResponse response) throws Exception{
        Utilities utilities = new Utilities();
        JasperPrint jasperPrint = prepareReport(templatePath, lstParameters, format);
        reportPrintName = loggedUser.getUserId()+"_"+reportPrintName+"_"+utilities.s7.format(new Date())+format.getExtension();
        ServletOutputStream outputStream = null;
        response.setHeader("Content-Disposition", "attachment; filename="+reportPrintName);
//        response.setContentLength(4096);
        switch(format){
            case DOCUMENT:
                response.setContentType("application/pdf");
                outputStream = response.getOutputStream();
                JRDocxExporter docExporter = new JRDocxExporter();
                docExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                docExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
                SimpleDocxExporterConfiguration docxConfig = new SimpleDocxExporterConfiguration();
                docExporter.setConfiguration(docxConfig);
                docExporter.exportReport();
                break;
            case PDF:
                response.setHeader("Content-Disposition", "inline; filename="+reportPrintName);
                response.setContentType("application/pdf");
                outputStream = response.getOutputStream();
                JRPdfExporter pdfExporter = new JRPdfExporter();
                pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
                SimplePdfExporterConfiguration pdfConfig = new SimplePdfExporterConfiguration();
                pdfExporter.setConfiguration(pdfConfig);
                pdfExporter.exportReport();
                break;
            case EXCEL:
                response.setContentType("application/vnd.ms-excel");
                outputStream = response.getOutputStream();
                JRXlsxExporter xlsxExporter = new JRXlsxExporter();
                xlsxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                xlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
                SimpleXlsxExporterConfiguration xlsConfig = new SimpleXlsxExporterConfiguration();
                xlsxExporter.setConfiguration(xlsConfig);
                xlsxExporter.exportReport();
                break;
        }
    }

    
}
