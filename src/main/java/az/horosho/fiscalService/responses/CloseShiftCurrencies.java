package az.horosho.fiscalService.responses;

import java.util.List;

public class CloseShiftCurrencies {
    private String currency;
    private int saleCount;
    private double saleSum;
    private double saleCashSum;
    private double saleCashlessSum;
    private double salePrepaymentSum;
    private double saleCreditSum;
    private double saleBonusSum;
    private List<CloseShiftVAT> saleVatAmounts;
    private int depositCount;
    private double depositSum;
    private int withdrawCount;
    private double withdrawSum;
    private int moneyBackCount;
    private double moneyBackSum;
    private double moneyBackCashSum;
    private double moneyBackCashlessSum;
    private double moneyBackPrepaymentSum;
    private double moneyBackCreditSum;
    private double moneyBackBonusSum;
    private List<CloseShiftVAT> moneyBackVatAmounts;
    private int rollbackCount;
    private double rollbackSum;
    private double rollbackCashSum;
    private double rollbackCashlessSum;
    private double rollbackPrepaymentSum;
    private double rollbackCreditSum;
    private double rollbackBonusSum;
    private List<CloseShiftVAT> rollbackVatAmounts;
    private int correctionCount;
    private double correctionSum;
    private double correctionCashSum;
    private double correctionCashlessSum;
    private double correctionPrepaymentSum;
    private double correctionCreditSum;
    private double correctionBonusSum;
    private List<CloseShiftVAT> correctionVatAmounts;
    private int prepayCount;
    private double prepaySum;
    private double prepayCashSum;
    private double prepayCashlessSum;
    private double prepayPrepaymentSum;
    private double prepayCreditSum;
    private double prepayBonusSum;
    private List<CloseShiftVAT> prepayVatAmounts;
    private int creditpayCount;
    private double creditpaySum;
    private double creditpayCashSum;
    private double creditpayCashlessSum;
    private double creditpayPrepaymentSum;
    private double creditpayCreditSum;
    private double creditpayBonusSum;
    private List<CloseShiftVAT> creditpayVatAmounts;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }

    public double getSaleSum() {
        return saleSum;
    }

    public void setSaleSum(double saleSum) {
        this.saleSum = saleSum;
    }

    public double getSaleCashSum() {
        return saleCashSum;
    }

    public void setSaleCashSum(double saleCashSum) {
        this.saleCashSum = saleCashSum;
    }

    public double getSaleCashlessSum() {
        return saleCashlessSum;
    }

    public void setSaleCashlessSum(double saleCashlessSum) {
        this.saleCashlessSum = saleCashlessSum;
    }

    public double getSalePrepaymentSum() {
        return salePrepaymentSum;
    }

    public void setSalePrepaymentSum(double salePrepaymentSum) {
        this.salePrepaymentSum = salePrepaymentSum;
    }

    public double getSaleCreditSum() {
        return saleCreditSum;
    }

    public void setSaleCreditSum(double saleCreditSum) {
        this.saleCreditSum = saleCreditSum;
    }

    public double getSaleBonusSum() {
        return saleBonusSum;
    }

    public void setSaleBonusSum(double saleBonusSum) {
        this.saleBonusSum = saleBonusSum;
    }

    public List<CloseShiftVAT> getSaleVatAmounts() {
        return saleVatAmounts;
    }

    public void setSaleCloseShiftVATs(List<CloseShiftVAT> saleCloseShiftVATs) {
        this.saleVatAmounts = saleCloseShiftVATs;
    }

    public int getDepositCount() {
        return depositCount;
    }

    public void setDepositCount(int depositCount) {
        this.depositCount = depositCount;
    }

    public double getDepositSum() {
        return depositSum;
    }

    public void setDepositSum(double depositSum) {
        this.depositSum = depositSum;
    }

    public int getWithdrawCount() {
        return withdrawCount;
    }

    public void setWithdrawCount(int withdrawCount) {
        this.withdrawCount = withdrawCount;
    }

    public double getWithdrawSum() {
        return withdrawSum;
    }

    public void setWithdrawSum(double withdrawSum) {
        this.withdrawSum = withdrawSum;
    }

    public int getMoneyBackCount() {
        return moneyBackCount;
    }

    public void setMoneyBackCount(int moneyBackCount) {
        this.moneyBackCount = moneyBackCount;
    }

    public double getMoneyBackSum() {
        return moneyBackSum;
    }

    public void setMoneyBackSum(double moneyBackSum) {
        this.moneyBackSum = moneyBackSum;
    }

    public double getMoneyBackCashSum() {
        return moneyBackCashSum;
    }

    public void setMoneyBackCashSum(double moneyBackCashSum) {
        this.moneyBackCashSum = moneyBackCashSum;
    }

    public double getMoneyBackCashlessSum() {
        return moneyBackCashlessSum;
    }

    public void setMoneyBackCashlessSum(double moneyBackCashlessSum) {
        this.moneyBackCashlessSum = moneyBackCashlessSum;
    }

    public double getMoneyBackPrepaymentSum() {
        return moneyBackPrepaymentSum;
    }

    public void setMoneyBackPrepaymentSum(double moneyBackPrepaymentSum) {
        this.moneyBackPrepaymentSum = moneyBackPrepaymentSum;
    }

    public double getMoneyBackCreditSum() {
        return moneyBackCreditSum;
    }

    public void setMoneyBackCreditSum(double moneyBackCreditSum) {
        this.moneyBackCreditSum = moneyBackCreditSum;
    }

    public double getMoneyBackBonusSum() {
        return moneyBackBonusSum;
    }

    public void setMoneyBackBonusSum(double moneyBackBonusSum) {
        this.moneyBackBonusSum = moneyBackBonusSum;
    }

    public List<CloseShiftVAT> getMoneyBackVatAmounts() {
        return moneyBackVatAmounts;
    }

    public void setSaleVatAmounts(List<CloseShiftVAT> moneyBackVatAmounts) {
        this.moneyBackVatAmounts = moneyBackVatAmounts;
    }

    public int getRollbackCount() {
        return rollbackCount;
    }

    public void setRollbackCount(int rollbackCount) {
        this.rollbackCount = rollbackCount;
    }

    public double getRollbackSum() {
        return rollbackSum;
    }

    public void setRollbackSum(double rollbackSum) {
        this.rollbackSum = rollbackSum;
    }

    public double getRollbackCashSum() {
        return rollbackCashSum;
    }

    public void setRollbackCashSum(double rollbackCashSum) {
        this.rollbackCashSum = rollbackCashSum;
    }

    public double getRollbackCashlessSum() {
        return rollbackCashlessSum;
    }

    public void setRollbackCashlessSum(double rollbackCashlessSum) {
        this.rollbackCashlessSum = rollbackCashlessSum;
    }

    public double getRollbackPrepaymentSum() {
        return rollbackPrepaymentSum;
    }

    public void setRollbackPrepaymentSum(double rollbackPrepaymentSum) {
        this.rollbackPrepaymentSum = rollbackPrepaymentSum;
    }

    public double getRollbackCreditSum() {
        return rollbackCreditSum;
    }

    public void setRollbackCreditSum(double rollbackCreditSum) {
        this.rollbackCreditSum = rollbackCreditSum;
    }

    public double getRollbackBonusSum() {
        return rollbackBonusSum;
    }

    public void setRollbackBonusSum(double rollbackBonusSum) {
        this.rollbackBonusSum = rollbackBonusSum;
    }

    public List<CloseShiftVAT> getRollbackVatAmounts() {
        return rollbackVatAmounts;
    }

    public void setRollbackCloseShiftVATs(List<CloseShiftVAT> rollbackVatAmounts) {
        this.rollbackVatAmounts = rollbackVatAmounts;
    }

    public int getCorrectionCount() {
        return correctionCount;
    }

    public void setCorrectionCount(int correctionCount) {
        this.correctionCount = correctionCount;
    }

    public double getCorrectionSum() {
        return correctionSum;
    }

    public void setCorrectionSum(double correctionSum) {
        this.correctionSum = correctionSum;
    }

    public double getCorrectionCashSum() {
        return correctionCashSum;
    }

    public void setCorrectionCashSum(double correctionCashSum) {
        this.correctionCashSum = correctionCashSum;
    }

    public double getCorrectionCashlessSum() {
        return correctionCashlessSum;
    }

    public void setCorrectionCashlessSum(double correctionCashlessSum) {
        this.correctionCashlessSum = correctionCashlessSum;
    }

    public double getCorrectionPrepaymentSum() {
        return correctionPrepaymentSum;
    }

    public void setCorrectionPrepaymentSum(double correctionPrepaymentSum) {
        this.correctionPrepaymentSum = correctionPrepaymentSum;
    }

    public double getCorrectionCreditSum() {
        return correctionCreditSum;
    }

    public void setCorrectionCreditSum(double correctionCreditSum) {
        this.correctionCreditSum = correctionCreditSum;
    }

    public double getCorrectionBonusSum() {
        return correctionBonusSum;
    }

    public void setCorrectionBonusSum(double correctionBonusSum) {
        this.correctionBonusSum = correctionBonusSum;
    }

    public List<CloseShiftVAT> getCorrectionCloseShiftVATs() {
        return correctionVatAmounts;
    }

    public void setCorrectionCloseShiftVATs(List<CloseShiftVAT> correctionVatAmounts) {
        this.correctionVatAmounts = correctionVatAmounts;
    }

    public int getPrepayCount() {
        return prepayCount;
    }

    public void setPrepayCount(int prepayCount) {
        this.prepayCount = prepayCount;
    }

    public double getPrepaySum() {
        return prepaySum;
    }

    public void setPrepaySum(double prepaySum) {
        this.prepaySum = prepaySum;
    }

    public double getPrepayCashSum() {
        return prepayCashSum;
    }

    public void setPrepayCashSum(double prepayCashSum) {
        this.prepayCashSum = prepayCashSum;
    }

    public double getPrepayCashlessSum() {
        return prepayCashlessSum;
    }

    public void setPrepayCashlessSum(double prepayCashlessSum) {
        this.prepayCashlessSum = prepayCashlessSum;
    }

    public double getPrepayPrepaymentSum() {
        return prepayPrepaymentSum;
    }

    public void setPrepayPrepaymentSum(double prepayPrepaymentSum) {
        this.prepayPrepaymentSum = prepayPrepaymentSum;
    }

    public double getPrepayCreditSum() {
        return prepayCreditSum;
    }

    public void setPrepayCreditSum(double prepayCreditSum) {
        this.prepayCreditSum = prepayCreditSum;
    }

    public double getPrepayBonusSum() {
        return prepayBonusSum;
    }

    public void setPrepayBonusSum(double prepayBonusSum) {
        this.prepayBonusSum = prepayBonusSum;
    }

    public List<CloseShiftVAT> getPrepayVatAmounts() {
        return prepayVatAmounts;
    }

    public void setPrepayCloseShiftVATs(List<CloseShiftVAT> prepayVatAmounts) {
        this.prepayVatAmounts = prepayVatAmounts;
    }

    public int getCreditpayCount() {
        return creditpayCount;
    }

    public void setCreditpayCount(int creditpayCount) {
        this.creditpayCount = creditpayCount;
    }

    public double getCreditpaySum() {
        return creditpaySum;
    }

    public void setCreditpaySum(double creditpaySum) {
        this.creditpaySum = creditpaySum;
    }

    public double getCreditpayCashSum() {
        return creditpayCashSum;
    }

    public void setCreditpayCashSum(double creditpayCashSum) {
        this.creditpayCashSum = creditpayCashSum;
    }

    public double getCreditpayCashlessSum() {
        return creditpayCashlessSum;
    }

    public void setCreditpayCashlessSum(double creditpayCashlessSum) {
        this.creditpayCashlessSum = creditpayCashlessSum;
    }

    public double getCreditpayPrepaymentSum() {
        return creditpayPrepaymentSum;
    }

    public void setCreditpayPrepaymentSum(double creditpayPrepaymentSum) {
        this.creditpayPrepaymentSum = creditpayPrepaymentSum;
    }

    public double getCreditpayCreditSum() {
        return creditpayCreditSum;
    }

    public void setCreditpayCreditSum(double creditpayCreditSum) {
        this.creditpayCreditSum = creditpayCreditSum;
    }

    public double getCreditpayBonusSum() {
        return creditpayBonusSum;
    }

    public void setCreditpayBonusSum(double creditpayBonusSum) {
        this.creditpayBonusSum = creditpayBonusSum;
    }

    public List<CloseShiftVAT> getCreditpayVatAmounts() {
        return creditpayVatAmounts;
    }

    public void setCreditpayCloseShiftVATs(List<CloseShiftVAT> creditpayVatAmounts) {
        this.creditpayVatAmounts = creditpayVatAmounts;
    }
}
