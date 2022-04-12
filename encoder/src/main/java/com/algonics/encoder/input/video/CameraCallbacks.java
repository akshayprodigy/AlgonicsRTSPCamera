package com.algonics.encoder.input.video;

public interface CameraCallbacks {
  void onCameraChanged(CameraHelper.Facing facing);
  void onCameraError(String error);
}
