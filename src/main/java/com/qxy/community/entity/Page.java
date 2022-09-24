package com.qxy.community.entity;

/**
 * @author qxy
 * @version 1.0
 * @Date 2022/8/27 22:50
 */

/**
 * 封装分页数据
 */
public class Page {
    private int rowCnt = 10;//每页条数
    private int pageCnt = 1;//当前页
    private int rows;//数据总条数，用于计算总页数
    private String path;//查询路径，用于复用分页链接

    public int getPageCnt() {
        return pageCnt;
    }

    public void setPageCnt(int pageCnt) {
        if(pageCnt>=1)
            this.pageCnt = pageCnt;
    }

    public int getRowCnt() {
        return rowCnt;
    }

    public void setRowCnt(int rowCnt) {
        if (rowCnt >= 1 && rowCnt <= 100) {
            this.rowCnt = rowCnt;
        }
    }

    /**
     * 获取当前页的起始行
     *
     * @return
     */
    public int getOffset() {
        return (pageCnt - 1) * rowCnt;
    }

    /**
     * 获取总页数
     *
     * @return
     */
    public int getTotal() {
        if (rows % rowCnt == 0) {
            return rows % rowCnt;
        } else {
            return rows % rowCnt + 1;
        }
    }

    /**
     * 获取起始页码
     *
     * @return
     */
    public int getFrom() {
        int from = pageCnt - 2;
        return from < 1 ? 1 : from;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * 获取结束页码
     *
     * @return
     */
    public int getTo() {
        int to = pageCnt + 2;
        int total = getTotal();
        return to > total ? total : to;
    }
}
