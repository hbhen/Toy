package toy.android.com.toy.bean;

/**
 * Created by Android on 2017/8/18.
 */

public class ToyLoginReqBean {
    /**
     * TYPE : REQ
     * CMD : LOG
     * ACCT : XXXX
     * TIME : 20161127101010000
     * BODY : {"LOGINNAME":"861575032099128","PASSWORD":"","DEVCODE":"861575032099128","OS":"A","VERSION":"1.0"}
     * VERI :
     * TOKEN :
     * SEQ : 1
     */

    private String TYPE;
    private String CMD;
    private String ACCT;
    private String TIME;
    private BODYBean BODY;
    private String VERI;
    private String TOKEN;
    private String SEQ;

    @Override
    public String toString() {
        return "ToyLoginReqBean{" +
                "TYPE='" + TYPE + '\'' +
                ", CMD='" + CMD + '\'' +
                ", ACCT='" + ACCT + '\'' +
                ", TIME='" + TIME + '\'' +
                ", BODY=" + BODY +
                ", VERI='" + VERI + '\'' +
                ", TOKEN='" + TOKEN + '\'' +
                ", SEQ='" + SEQ + '\'' +
                '}';
    }

    public ToyLoginReqBean(String TYPE, String CMD, String ACCT, String TIME, BODYBean BODY, String VERI, String TOKEN, String SEQ) {
        this.TYPE = TYPE;
        this.CMD = CMD;
        this.ACCT = ACCT;
        this.TIME = TIME;
        this.BODY = BODY;
        this.VERI = VERI;
        this.TOKEN = TOKEN;
        this.SEQ = SEQ;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public String getCMD() {
        return CMD;
    }

    public void setCMD(String CMD) {
        this.CMD = CMD;
    }

    public String getACCT() {
        return ACCT;
    }

    public void setACCT(String ACCT) {
        this.ACCT = ACCT;
    }

    public String getTIME() {
        return TIME;
    }

    public void setTIME(String TIME) {
        this.TIME = TIME;
    }

    public BODYBean getBODY() {
        return BODY;
    }

    public void setBODY(BODYBean BODY) {
        this.BODY = BODY;
    }

    public String getVERI() {
        return VERI;
    }

    public void setVERI(String VERI) {
        this.VERI = VERI;
    }

    public String getTOKEN() {
        return TOKEN;
    }

    public void setTOKEN(String TOKEN) {
        this.TOKEN = TOKEN;
    }

    public String getSEQ() {
        return SEQ;
    }

    public void setSEQ(String SEQ) {
        this.SEQ = SEQ;
    }

    public static class BODYBean {
        /**
         * LOGINNAME : 861575032099128
         * PASSWORD :
         * DEVCODE : 861575032099128
         * OS : A
         * VERSION : 1.0
         */

        private String LOGINNAME;
        private String PASSWORD;
        private String DEVCODE;
        private String OS;
        private String VERSION;

        @Override
        public String toString() {
            return "BODYBean{" +
                    "LOGINNAME='" + LOGINNAME + '\'' +
                    ", PASSWORD='" + PASSWORD + '\'' +
                    ", DEVCODE='" + DEVCODE + '\'' +
                    ", OS='" + OS + '\'' +
                    ", VERSION='" + VERSION + '\'' +
                    '}';
        }

        public BODYBean(String LOGINNAME, String PASSWORD, String DEVCODE, String OS, String VERSION) {
            this.LOGINNAME = LOGINNAME;
            this.PASSWORD = PASSWORD;
            this.DEVCODE = DEVCODE;
            this.OS = OS;
            this.VERSION = VERSION;
        }

        public String getLOGINNAME() {
            return LOGINNAME;
        }

        public void setLOGINNAME(String LOGINNAME) {
            this.LOGINNAME = LOGINNAME;
        }

        public String getPASSWORD() {
            return PASSWORD;
        }

        public void setPASSWORD(String PASSWORD) {
            this.PASSWORD = PASSWORD;
        }

        public String getDEVCODE() {
            return DEVCODE;
        }

        public void setDEVCODE(String DEVCODE) {
            this.DEVCODE = DEVCODE;
        }

        public String getOS() {
            return OS;
        }

        public void setOS(String OS) {
            this.OS = OS;
        }

        public String getVERSION() {
            return VERSION;
        }

        public void setVERSION(String VERSION) {
            this.VERSION = VERSION;
        }
    }
}
