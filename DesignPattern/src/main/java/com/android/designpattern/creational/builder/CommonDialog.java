package com.android.designpattern.creational.builder;

public class CommonDialog {

    private Params mParams;

    private CommonDialog(Params params) {
        this.mParams = params;
    }

    public void setTitleText(String title) {
        mParams.titleText = title;
    }

    public void setMessageText(String message) {
        mParams.messageText = message;
    }

    public void show() {
        //set view...
    }

    public static class Builder {

        protected Params mParams = new Params();

        public Builder setTitleText(String title) {
            mParams.titleText = title;
            return this;
        }

        public Builder setMessageText(String message) {
            mParams.messageText = message;
            return this;
        }

        public Builder setConfirmText(String confirm) {
            mParams.confirmText = confirm;
            return this;
        }

        public Builder setCancelText(String cancel) {
            mParams.cancelText = cancel;
            return this;
        }

        public CommonDialog create() {
            if (mParams.confirmText == null) mParams.confirmText = "ok";
            return new CommonDialog(mParams);
        }

        public CommonDialog createOnlyConfirm() {
            mParams.cancelText = null;
            return new CommonDialog(mParams);
        }

    }

    private static class Params {

        private String titleText;
        private String messageText;

        private String confirmText;
        private String cancelText;

    }

}
