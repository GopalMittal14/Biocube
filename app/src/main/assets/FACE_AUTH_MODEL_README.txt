Face Authentication Model (TensorFlow Lite)
=========================================

This app expects a face embedding model at:

  app/src/main/assets/face_auth.tflite

Model requirements:
- Input: 1xHxWx3 RGB image tensor
  - FLOAT32 models are fed with FaceNet-style normalization: (x - 127.5) / 128.0
  - UINT8 models are fed with raw 0..255 bytes
- Output: 1xD embedding vector (D can be 128/192/512 etc.)

Enrollment:
- Use "Face Scan" from User Trainings to capture and save an embedding.

Authentication:
- On app start, if a user is already logged in AND an enrollment exists,
  Splash will require Face Authentication before continuing.

Tuning:
- The cosine similarity threshold is configured in:
  com.biocube.app.data.faceauth.FaceModelConfig

