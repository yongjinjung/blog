package com.jinblog.api.request;

public class CarBuilder {

    private String engine; //엔진
    private boolean airbag; // 에어백 여부
    private String color; // 차량색상
    private boolean cameraSensor; // 카메라센서 유무
    private boolean AEB; // 자동급제동장치 유무

    public CarBuilder setEngine(String engine) {
        this.engine = engine;
        return this;
    }

    public CarBuilder setAirbag(boolean airbag) {
        this.airbag = airbag;
        return this;
    }

    public CarBuilder setColor(String color) {
        this.color = color;
        return this;
    }

    public CarBuilder setCameraSensor(boolean cameraSensor) {
        this.cameraSensor = cameraSensor;
        return this;
    }

    public CarBuilder setAEB(boolean AEB) {
        this.AEB = AEB;
        return this;
    }

    public Car build(){
        return new Car(engine, airbag, color, cameraSensor, AEB);
    }
}
