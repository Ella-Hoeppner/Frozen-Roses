(ns frozen-roses.shaders)

(def conv-source
  "precision highp float;
  uniform vec2 time;
  uniform vec2 size;
  uniform sampler2D uSampler;
  float sinh(float x)
  {
    return ((exp(x)) - (exp((0.0 - x)))) / 2.0;
  }
  float cosh(float x)
  {
      return ((exp(x)) + (exp((0.0 - x)))) / 2.0;
  }
  float tanh(float x)
  {
    return (sinh(x)) / (cosh(x));
  }
  float sigmoid(float x)
  {
    return 1.0 / (1.0 + (exp((0.0 - x))));
  }
  float f(float input0, float input1, float input2, float input3)
  {
    float stackValue0=0.0;
    float stackValue1=0.0;
    float stackValue2=0.0;
    float stackValue3=0.0;
    float arg0=0.0;
    float arg1=0.0;
    float arg2=0.0;
    float arg3=0.0;
    //Input stack values;
    stackValue0 = input0;
    stackValue1 = input1;
    stackValue2 = input2;
    stackValue3 = input3;
    //OP: c-*;
    arg0 = stackValue0;
    arg1 = stackValue1;
    arg2 = stackValue2;
    arg3 = stackValue3;
    stackValue0 = ((arg0 * arg2) - (arg1 * arg3));
    stackValue1 = ((arg0 * arg3) + (arg1 * arg2));
    //OP: abs;
    arg0 = stackValue1;
    stackValue1 = (abs(arg0));
    //OP: sin;
    arg0 = stackValue1;
    stackValue1 = (sin(arg0));
    //OP: +;
    arg0 = stackValue0;
    arg1 = stackValue1;
    stackValue0 = (arg0 + arg1);
    //OP: sinh;
    arg0 = stackValue0;
    stackValue0 = (sinh(arg0));
    //OP: ln;
    arg0 = stackValue0;
    stackValue0 = ((sign(arg0)) * (log((abs(arg0)))));
    return stackValue0;
  }
  void main()
  {
    float x = (gl_FragCoord.x / size.x);
    float y = (gl_FragCoord.y / size.y);
    float value = (sigmoid((f(((texture2D(uSampler, (vec2((x + 0.00000000), (y + 0.02500000))))).x), ((texture2D(uSampler, (vec2((x + 0.02500000), (y + 0.00000000))))).x), ((texture2D(uSampler, (vec2((x + 0.00000000), (y + -0.02500000))))).x), ((texture2D(uSampler, (vec2((x + -0.02500000), (y + 0.00000000))))).x)))));
    gl_FragColor = (vec4(value, 0.0, 0.0, 1.0));
  }")

(def colormap-source
  "precision highp float;
  uniform vec2 time;
  uniform vec2 size;
  uniform sampler2D colorSpace;
  uniform sampler2D uSampler;
  float sinh(float x)
  {
    return ((exp(x)) - (exp((0.0 - x)))) / 2.0;
  }
  float cosh(float x)
  {
    return ((exp(x)) + (exp((0.0 - x)))) / 2.0;
  }
  float tanh(float x)
  {
    return (sinh(x)) / (cosh(x));
  }
  float sigmoid(float x)
  {
    return 1.0 / (1.0 + (exp((0.0 - x))));
  }
  vec2 f(float input0, float input1, float input2)
  {
    float stackValue0=0.0;
    float stackValue1=0.0;
    float stackValue2=0.0;
    float arg0=0.0;
    float arg1=0.0;
    //Input stack values;
    stackValue0 = input0;
    stackValue1 = input1;
    stackValue2 = input2;
    //OP: c-sin;
    arg0 = stackValue1;
    arg1 = stackValue2;
    stackValue1 = ((sin(arg0)) * (cosh((arg1))));
    stackValue2 = (((cos(arg0))) * (sinh((arg1))));
    //OP: sigmoid;
    arg0 = stackValue2;
    stackValue2 = (sigmoid(arg0));
    //OP: c-exp;
    arg0 = stackValue1;
    arg1 = stackValue2;
    stackValue1 = ((exp(arg0)) * (cos(arg1)));
    stackValue2 = ((exp(arg0)) * (sin(arg1)));
    //OP: c-tanh;
    arg0 = stackValue1;
    arg1 = stackValue2;
    stackValue1 = ((sinh((2.0 * arg0))) / ((cosh((2.0 * arg0))) + (cos((2.0 * arg1)))));
    stackValue2 = ((sin((2.0 * arg1))) / ((cosh((2.0 * arg0))) + (cos((2.0 * arg1)))));
    //OP: drop;
    arg0 = stackValue2;
    return vec2(stackValue1, stackValue0);
  }
  void main()
  {
    float x = 0.0;
    float y = 0.0;
    vec2 fOut = (vec2(0.0, 0.0));
    x = ((gl_FragCoord.x + 0.12500000) / size.x);
    y = ((gl_FragCoord.y + 0.12500000) / size.y);
    fOut = (f(((texture2D(uSampler, (vec2(x, y)))).x), (0.01 * time.x), (0.01 * time.y)));
    vec4 color0 = (texture2D(colorSpace, (vec2((sigmoid(fOut.x)), (sigmoid(fOut.y))))));
    x = ((gl_FragCoord.x + 0.37500000) / size.x);
    y = ((gl_FragCoord.y + 0.12500000) / size.y);
    fOut = (f(((texture2D(uSampler, (vec2(x, y)))).x), (0.01 * time.x), (0.01 * time.y)));
    vec4 color1 = (texture2D(colorSpace, (vec2((sigmoid(fOut.x)), (sigmoid(fOut.y))))));
    x = ((gl_FragCoord.x + 0.62500000) / size.x);
    y = ((gl_FragCoord.y + 0.12500000) / size.y);
    fOut = (f(((texture2D(uSampler, (vec2(x, y)))).x), (0.01 * time.x), (0.01 * time.y)));
    vec4 color2 = (texture2D(colorSpace, (vec2((sigmoid(fOut.x)), (sigmoid(fOut.y))))));
    x = ((gl_FragCoord.x + 0.87500000) / size.x);
    y = ((gl_FragCoord.y + 0.12500000) / size.y);
    fOut = (f(((texture2D(uSampler, (vec2(x, y)))).x), (0.01 * time.x), (0.01 * time.y)));
    vec4 color3 = (texture2D(colorSpace, (vec2((sigmoid(fOut.x)), (sigmoid(fOut.y))))));
    x = ((gl_FragCoord.x + 0.12500000) / size.x);
    y = ((gl_FragCoord.y + 0.37500000) / size.y);
    fOut = (f(((texture2D(uSampler, (vec2(x, y)))).x), (0.01 * time.x), (0.01 * time.y)));
    vec4 color4 = (texture2D(colorSpace, (vec2((sigmoid(fOut.x)), (sigmoid(fOut.y))))));
    x = ((gl_FragCoord.x + 0.37500000) / size.x);
    y = ((gl_FragCoord.y + 0.37500000) / size.y);
    fOut = (f(((texture2D(uSampler, (vec2(x, y)))).x), (0.01 * time.x), (0.01 * time.y)));
    vec4 color5 = (texture2D(colorSpace, (vec2((sigmoid(fOut.x)), (sigmoid(fOut.y))))));
    x = ((gl_FragCoord.x + 0.62500000) / size.x);
    y = ((gl_FragCoord.y + 0.37500000) / size.y);
    fOut = (f(((texture2D(uSampler, (vec2(x, y)))).x), (0.01 * time.x), (0.01 * time.y)));
    vec4 color6 = (texture2D(colorSpace, (vec2((sigmoid(fOut.x)), (sigmoid(fOut.y))))));
    x = ((gl_FragCoord.x + 0.87500000) / size.x);
    y = ((gl_FragCoord.y + 0.37500000) / size.y);
    fOut = (f(((texture2D(uSampler, (vec2(x, y)))).x), (0.01 * time.x), (0.01 * time.y)));
    vec4 color7 = (texture2D(colorSpace, (vec2((sigmoid(fOut.x)), (sigmoid(fOut.y))))));
    x = ((gl_FragCoord.x + 0.12500000) / size.x);
    y = ((gl_FragCoord.y + 0.62500000) / size.y);
    fOut = (f(((texture2D(uSampler, (vec2(x, y)))).x), (0.01 * time.x), (0.01 * time.y)));
    vec4 color8 = (texture2D(colorSpace, (vec2((sigmoid(fOut.x)), (sigmoid(fOut.y))))));
    x = ((gl_FragCoord.x + 0.37500000) / size.x);
    y = ((gl_FragCoord.y + 0.62500000) / size.y);
    fOut = (f(((texture2D(uSampler, (vec2(x, y)))).x), (0.01 * time.x), (0.01 * time.y)));
    vec4 color9 = (texture2D(colorSpace, (vec2((sigmoid(fOut.x)), (sigmoid(fOut.y))))));
    x = ((gl_FragCoord.x + 0.62500000) / size.x);
    y = ((gl_FragCoord.y + 0.62500000) / size.y);
    fOut = (f(((texture2D(uSampler, (vec2(x, y)))).x), (0.01 * time.x), (0.01 * time.y)));
    vec4 color10 = (texture2D(colorSpace, (vec2((sigmoid(fOut.x)), (sigmoid(fOut.y))))));
    x = ((gl_FragCoord.x + 0.87500000) / size.x);
    y = ((gl_FragCoord.y + 0.62500000) / size.y);
    fOut = (f(((texture2D(uSampler, (vec2(x, y)))).x), (0.01 * time.x), (0.01 * time.y)));
    vec4 color11 = (texture2D(colorSpace, (vec2((sigmoid(fOut.x)), (sigmoid(fOut.y))))));
    x = ((gl_FragCoord.x + 0.12500000) / size.x);
    y = ((gl_FragCoord.y + 0.87500000) / size.y);
    fOut = (f(((texture2D(uSampler, (vec2(x, y)))).x), (0.01 * time.x), (0.01 * time.y)));
    vec4 color12 = (texture2D(colorSpace, (vec2((sigmoid(fOut.x)), (sigmoid(fOut.y))))));
    x = ((gl_FragCoord.x + 0.37500000) / size.x);
    y = ((gl_FragCoord.y + 0.87500000) / size.y);
    fOut = (f(((texture2D(uSampler, (vec2(x, y)))).x), (0.01 * time.x), (0.01 * time.y)));
    vec4 color13 = (texture2D(colorSpace, (vec2((sigmoid(fOut.x)), (sigmoid(fOut.y))))));
    x = ((gl_FragCoord.x + 0.62500000) / size.x);
    y = ((gl_FragCoord.y + 0.87500000) / size.y);
    fOut = (f(((texture2D(uSampler, (vec2(x, y)))).x), (0.01 * time.x), (0.01 * time.y)));
    vec4 color14 = (texture2D(colorSpace, (vec2((sigmoid(fOut.x)), (sigmoid(fOut.y))))));
    x = ((gl_FragCoord.x + 0.87500000) / size.x);
    y = ((gl_FragCoord.y + 0.87500000) / size.y);
    fOut = (f(((texture2D(uSampler, (vec2(x, y)))).x), (0.01 * time.x), (0.01 * time.y)));
    vec4 color15 = (texture2D(colorSpace, (vec2((sigmoid(fOut.x)), (sigmoid(fOut.y))))));
    gl_FragColor = ((color0 + color1 + color2 + color3 + color4 + color5 + color6 + color7 + color8 + color9 + color10 + color11 + color12 + color13 + color14 + color15) / 16.0);
  }")