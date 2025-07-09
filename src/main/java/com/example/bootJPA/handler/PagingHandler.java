package com.example.bootJPA.handler;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.List;

@ToString
@Getter
public class PagingHandler<T> {
  private int startPage;
  private int endPage;
  private int totalPage;
  private long totalCount;
  private boolean hasPrev, hasNext;
  private int pageNo;
  List<T> list;

  private String type;
  private String keyword;

  public PagingHandler(Page<T> page) {
    this(page, 10, null, null);
  }

  /**
   * Creates a paging handler with default group size = 10.
   */
  public PagingHandler(Page<T> page, String type, String keyword) {
    this(page, 10, type, keyword);
  }

  /**
   * Creates a paging handler with custom group size.
   */
  public PagingHandler(Page<T> page, int groupSize, String type, String keyword) {
    this.pageNo = page.getNumber() + 1;
    this.totalPage = page.getTotalPages();
    this.totalCount = page.getTotalElements();

    this.startPage = page.getNumber() / groupSize * groupSize + 1;
    this.endPage = this.startPage + groupSize - 1;
    if (this.endPage > this.totalPage) this.endPage = this.totalPage;

    hasPrev = startPage > 1;
    hasNext = endPage < totalPage;

    list = page.toList();

    this.type = type;
    this.keyword = keyword;
  }
}
