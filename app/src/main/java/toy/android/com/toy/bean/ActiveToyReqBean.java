package toy.android.com.toy.bean;

/**
 * Created by Android on 2017/9/5.
 */

public class ActiveToyReqBean {

    /**
     * TYPE : REQ
     * CMD : HEART
     * ACCT : XXXX
     * TIME : 20161127101010000
     * BODY : {"OS":"A","TYPE":"TOY","VERSION":"","ELEC":"10","WIFI":"10","MIC":"1","CAMERA":"1","VOL":"50",
     * "DEVCODE":"170976fa8aba5cc7b49"}
     * VERI :
     * TOKEN : 1b143124-8d21-4118-a35e-7b245b563fda
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
        return "ActiveToyReqBean{" +
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

    public ActiveToyReqBean(String TYPE, String CMD, String ACCT, String TIME, BODYBean BODY, String VERI, String
            TOKEN, String SEQ) {
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
         * OS : A
         * TYPE : TOY
         * VERSION :
         * ELEC : 10
         * WIFI : 10
         * MIC : 1
         * CAMERA : 1
         * VOL : 50
         * DEVCODE : 170976fa8aba5cc7b49
         */

        private String OS;
        private String TYPE;
        private String VERSION;
        private String ELEC;
        private String WIFI;
        private String MIC;
        private String CAMERA;
        private String VOL;
        private String DEVCODE;

        @Override
        public String toString() {
            return "BODYBean{" +
                    "OS='" + OS + '\'' +
                    ", TYPE='" + TYPE + '\'' +
                    ", VERSION='" + VERSION + '\'' +
                    ", ELEC='" + ELEC + '\'' +
                    ", WIFI='" + WIFI + '\'' +
                    ", MIC='" + MIC + '\'' +
                    ", CAMERA='" + CAMERA + '\'' +
                    ", VOL='" + VOL + '\'' +
                    ", DEVCODE='" + DEVCODE + '\'' +
                    '}';
        }

        public BODYBean(String OS, String TYPE, String VERSION, String ELEC, String WIFI, String MIC, String CAMERA,
                        String VOL, String DEVCODE) {
            this.OS = OS;
            this.TYPE = TYPE;
            this.VERSION = VERSION;
            this.ELEC = ELEC;
            this.WIFI = WIFI;
            this.MIC = MIC;
            this.CAMERA = CAMERA;
            this.VOL = VOL;
            this.DEVCODE = DEVCODE;
        }

        public String getOS() {
            return OS;
        }

        public void setOS(String OS) {
            this.OS = OS;
        }

        public String getTYPE() {
            return TYPE;
        }

        public void setTYPE(String TYPE) {
            this.TYPE = TYPE;
        }

        public String getVERSION() {
            return VERSION;
        }

        public void setVERSION(String VERSION) {
            this.VERSION = VERSION;
        }

        public String getELEC() {
            return ELEC;
        }

        public void setELEC(String ELEC) {
            this.ELEC = ELEC;
        }

        public String getWIFI() {
            return WIFI;
        }

        public void setWIFI(String WIFI) {
            this.WIFI = WIFI;
        }

        public String getMIC() {
            return MIC;
        }

        public void setMIC(String MIC) {
            this.MIC = MIC;
        }

        public String getCAMERA() {
            return CAMERA;
        }

        public void setCAMERA(String CAMERA) {
            this.CAMERA = CAMERA;
        }

        public String getVOL() {
            return VOL;
        }

        public void setVOL(String VOL) {
            this.VOL = VOL;
        }

        public String getDEVCODE() {
            return DEVCODE;
        }

        public void setDEVCODE(String DEVCODE) {
            this.DEVCODE = DEVCODE;
        }
    }
}
