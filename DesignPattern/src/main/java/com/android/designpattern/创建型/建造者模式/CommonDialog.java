package com.android.designpattern.创建型.建造者模式;

public class CommonDialog {

    private Params mParams;

    private CommonDialog(Params params) {
        this.mParams = params;
    }

    public void show() {
        //...
        //setTitle(mParams.titleText)
        //...
    }

    public static class Builder {

        enum TYPE {
            TYPE_NORMAL, TYPE_ONLY_CONFIRM, TYPE_ONLY_MESSAGE
        }

        protected Params mParams = new Params();

        private final Builder builder;

        public Builder() {
            this(TYPE.TYPE_NORMAL);
        }

        public Builder(TYPE type) {
            builder = new Builder();
            switch (type) {
                case TYPE_NORMAL:
                    buildNormal();
                    break;
                case TYPE_ONLY_CONFIRM:
                    buildOnlyConfirm();
                    break;
                case TYPE_ONLY_MESSAGE:
                    buildOnlyMessage();
                    break;
            }
        }

        private void buildNormal() {
            mParams.cancelable = true;
            mParams.onlyMessageText = false;
            mParams.onlyConfirmButton = false;
            mParams.confirmText = "Confirm";
            mParams.cancelText = "Cancel";
        }

        private void buildOnlyConfirm() {
            buildNormal();
        }

        private void buildOnlyMessage() {
        }

        public Builder setTitleText(String title) {
            mParams.titleText = title;
            return builder;
        }

        public Builder setMessageText(String message) {
            mParams.messageText = message;
            return builder;
        }

        public Builder setConfirmText(String confirm) {
            mParams.confirmText = confirm;
            return builder;
        }

        public Builder setCancelText(String cancel) {
            mParams.cancelText = cancel;
            return builder;
        }

        public Builder setCancelable(boolean cancelable) {
            mParams.cancelable = cancelable;
            return builder;
        }

        public Builder setOnlyMessageText(boolean onlyMessageText) {
            mParams.onlyMessageText = onlyMessageText;
            return builder;
        }

        public Builder setOnlyConfirmButton(boolean onlyConfirmButton) {
            mParams.onlyConfirmButton = onlyConfirmButton;
            return builder;
        }

        public CommonDialog create() {
            return new CommonDialog(mParams);
        }

    }

    private static class Params {

        private String titleText;
        private String messageText;

        private String confirmText;
        private String cancelText;

        private boolean cancelable;
        private boolean onlyMessageText;
        private boolean onlyConfirmButton;

    }

}
