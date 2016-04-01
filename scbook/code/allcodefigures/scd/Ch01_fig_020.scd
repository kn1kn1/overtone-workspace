1.20 Offset and Scale

(
	{
	var wave, label, scale, offset;
	wave = SinOsc.kr(1/10); // change to 400
	scale = 1;
	offset = 0;
//	wave = wave * scale + offset;
	label = "scale = " ++ scale.asString ++ ", offset = " ++ offset.asString;
	wave.round(0.01).poll(label: label);
	}.scope(1)
)
