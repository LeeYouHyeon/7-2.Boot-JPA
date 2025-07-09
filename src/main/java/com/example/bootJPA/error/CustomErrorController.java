package com.example.bootJPA.error;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class CustomErrorController implements ErrorController {
  private final String VIEW_PATH = "/error/";

  @RequestMapping("/error") // 전송 메서드에 상관없이 받아달라는 뜻
  public String handlerError(HttpServletRequest request) {
    log.info(">>>> 1");
    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    log.info(">>>> status {} ", status.toString());

    int statusCode = Integer.parseInt(status.toString());
    log.info(">>>> 2");
    if (statusCode == HttpStatus.NOT_FOUND.value()) { //404
      log.info(">>>> 3 404Page");
      return VIEW_PATH + "404";
    }
    if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
      log.info(">>>> 3 500Page");
      return VIEW_PATH + "500";
    }

    log.info(">>>> 3 Unknown Error");
    return "/";
  }
}
