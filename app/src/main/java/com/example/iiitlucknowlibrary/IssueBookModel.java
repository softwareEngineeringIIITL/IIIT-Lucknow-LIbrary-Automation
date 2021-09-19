package com.example.iiitlucknowlibrary;

public class IssueBookModel {
    String id;
    String issueDate;
    String returnDate;

    public IssueBookModel() {
    }

    public IssueBookModel(String id, String issueDate, String returnDate) {
        this.id=id;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }
}