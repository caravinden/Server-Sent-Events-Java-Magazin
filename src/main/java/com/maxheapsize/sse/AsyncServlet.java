package com.maxheapsize.sse;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@WebServlet(value = "/event", loadOnStartup = 1, asyncSupported = true)
public class AsyncServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {

    System.out.println("Entering the Servlet. ("+new Date().toString()+")");

    // start Async processing
    final AsyncContext asyncContext = request.startAsync();

    final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

    // send the events in 5 seconds from now, every 2 seconds
   executorService.scheduleWithFixedDelay(new ServerEventsSender(asyncContext.getResponse()), 5, 2, TimeUnit.SECONDS);

    // close the AsyncContext after 30 seconds, processing ends
    executorService.schedule(new Runnable() {
      @Override public void run() {
        executorService.shutdown();
        asyncContext.complete();
      }
    }, 30, TimeUnit.SECONDS);

    System.out.println("Done with the Servlet. ("+new Date().toString()+")");
  }
}
