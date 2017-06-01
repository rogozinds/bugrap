package my.vaadin.bugrap.layouts;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.vaadin.shared.Registration;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;

import my.vaadin.bugrap.UploadDesign;

public class UploadDesignWidget extends UploadDesign implements Upload.ProgressListener, Upload.FailedListener,
		Upload.SucceededListener, Upload.FinishedListener, Receiver {

	private ByteArrayOutputStream stream = new ByteArrayOutputStream();

	public UploadDesignWidget(String fileName) {
		fileNameLbl.setValue(fileName);
		init();
	}

	public Registration addDeleteAttachmentHandler(ClickListener listener) {
		return cancelBtn.addClickListener(listener);
	}

	private void init() {

	}

	@Override
	public void uploadFinished(FinishedEvent event) {
		progressUpld.setVisible(false);
		Notification.show("" + stream.toString());
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		Notification.show("Upload succeeded " + event.getFilename());
	}

	@Override
	public void uploadFailed(FailedEvent event) {
		Notification.show("Upload failed: " + event.getFilename(), Type.ERROR_MESSAGE);
	}

	@Override
	public void updateProgress(long readBytes, long contentLength) {
		progressUpld.setValue((float) (readBytes * 1.0 / contentLength));
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		return stream;
	}

	public byte[] getAttachment() {
		return stream.toByteArray();
	}
}
