package com.algonics.encoder.input.gl.render.filters;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.algonics.encoder.R;
import com.algonics.encoder.utils.gl.GlUtil;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by pedro on 4/02/18.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class HalftoneLinesFilterRender extends BaseFilterRender {

  //rotation matrix
  private final float[] squareVertexDataFilter = {
      // X, Y, Z, U, V
      -1f, -1f, 0f, 0f, 0f, //bottom left
      1f, -1f, 0f, 1f, 0f, //bottom right
      -1f, 1f, 0f, 0f, 1f, //top left
      1f, 1f, 0f, 1f, 1f, //top right
  };

  private int program = -1;
  private int aPositionHandle = -1;
  private int aTextureHandle = -1;
  private int uMVPMatrixHandle = -1;
  private int uSTMatrixHandle = -1;
  private int uSamplerHandle = -1;
  private int uResolutionHandle = -1;
  private int uModeHandle = -1;
  private int uRowsHandle = -1;
  private int uRotationHandle = -1;
  private int uAntialiasHandle = -1;
  private int uSampleDistHandle = -1;

  private float mode = 1f;
  private float rows = 40f;
  private float rotation = 0f;
  private float antialias = 0.2f;
  private float[] sampleDist = new float[] { 2f, 2f };

  public HalftoneLinesFilterRender() {
    squareVertex = ByteBuffer.allocateDirect(squareVertexDataFilter.length * FLOAT_SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer();
    squareVertex.put(squareVertexDataFilter).position(0);
    Matrix.setIdentityM(MVPMatrix, 0);
    Matrix.setIdentityM(STMatrix, 0);
  }

  @Override
  protected void initGlFilter(Context context) {
    String vertexShader = GlUtil.getStringFromRaw(context, R.raw.simple_vertex);
    String fragmentShader = GlUtil.getStringFromRaw(context, R.raw.halftone_lines_fragment);

    program = GlUtil.createProgram(vertexShader, fragmentShader);
    aPositionHandle = GLES20.glGetAttribLocation(program, "aPosition");
    aTextureHandle = GLES20.glGetAttribLocation(program, "aTextureCoord");
    uMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
    uSTMatrixHandle = GLES20.glGetUniformLocation(program, "uSTMatrix");
    uSamplerHandle = GLES20.glGetUniformLocation(program, "uSampler");
    uResolutionHandle = GLES20.glGetUniformLocation(program, "uResolution");
    uModeHandle = GLES20.glGetUniformLocation(program, "uMode");
    uRowsHandle = GLES20.glGetUniformLocation(program, "uRows");
    uRotationHandle = GLES20.glGetUniformLocation(program, "uRotation");
    uAntialiasHandle = GLES20.glGetUniformLocation(program, "uAntialias");
    uSampleDistHandle = GLES20.glGetUniformLocation(program, "uSampleDist");
  }

  @Override
  protected void drawFilter() {
    GLES20.glUseProgram(program);

    squareVertex.position(SQUARE_VERTEX_DATA_POS_OFFSET);
    GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false,
        SQUARE_VERTEX_DATA_STRIDE_BYTES, squareVertex);
    GLES20.glEnableVertexAttribArray(aPositionHandle);

    squareVertex.position(SQUARE_VERTEX_DATA_UV_OFFSET);
    GLES20.glVertexAttribPointer(aTextureHandle, 2, GLES20.GL_FLOAT, false,
        SQUARE_VERTEX_DATA_STRIDE_BYTES, squareVertex);
    GLES20.glEnableVertexAttribArray(aTextureHandle);

    GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MVPMatrix, 0);
    GLES20.glUniformMatrix4fv(uSTMatrixHandle, 1, false, STMatrix, 0);
    GLES20.glUniform2f(uResolutionHandle, getWidth(), getHeight());
    GLES20.glUniform1f(uModeHandle, mode);
    GLES20.glUniform1f(uRowsHandle, rows);
    GLES20.glUniform1f(uRotationHandle, rotation);
    GLES20.glUniform1f(uAntialiasHandle, antialias);
    GLES20.glUniform2f(uSampleDistHandle, sampleDist[0], sampleDist[1]);

    GLES20.glUniform1i(uSamplerHandle, 4);
    GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, previousTexId);
  }

  @Override
  public void release() {
    GLES20.glDeleteProgram(program);
  }

  public float getMode() {
    return mode;
  }

  public float getRows() {
    return rows;
  }

  public float getRotation() {
    return rotation;
  }

  public float getAntialias() {
    return antialias;
  }

  public float[] getSampleDist() {
    return sampleDist;
  }

  /**
   * @param mode 1 to 7 values
   */
  public void setMode(int mode) {
    if (mode < 1) {
      this.mode = 1;
    } else if (mode > 7) {
      this.mode = 7;
    } else {
      this.mode = mode;
    }
  }

  public void setRows(float rows) {
    this.rows = rows;
  }

  public void setRotation(float rotation) {
    this.rotation = rotation;
  }

  public void setAntialias(float antialias) {
    this.antialias = antialias;
  }

  public void setSampleDist(float[] sampleDist) {
    this.sampleDist = sampleDist;
  }
}
