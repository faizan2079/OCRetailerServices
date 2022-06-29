/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oc.controller;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
/**
 *
 * @author DELL PRECISION M6800
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

@Provider
public class GensonCustomResolver implements ContextResolver<Genson> {
  private SimpleDateFormat formatter = null;
  private Genson genson = null;
    public GensonCustomResolver() {
        this.formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        this.formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        genson = new GensonBuilder()
          .useDateAsTimestamp(false)
          .setSkipNull(true)
                .useDateFormat(formatter)
          .create();
    }
  @Override
  public Genson getContext(Class<?> type) {
    return genson;
  }
}