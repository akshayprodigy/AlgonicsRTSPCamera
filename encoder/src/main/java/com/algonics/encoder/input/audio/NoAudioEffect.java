package com.algonics.encoder.input.audio;

public class NoAudioEffect extends CustomAudioEffect {

  @Override
  public byte[] process(byte[] pcmBuffer) {
    return pcmBuffer;
  }
}
