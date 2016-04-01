1.21 SinOsc Offset and Scaled for Control

(
	{
	var control, scale, offset;
	// control = SinOsc.ar(6).scope("control"); // LFO
	control = LFNoise1.ar(100).scope(""); // LFO

	scale = 300; // try other values, but not greater than offset
	offset = 600; // try other values
	control = control * scale + offset;

	SinOsc.ar(freq: abs(control).poll, mul: 0.1).scope("audio")
	}.play
)

// Speaking of vibrato, I can't resist this faux Theremin:
{SinOsc.ar(SinOsc.ar(8, 0, 10, MouseX.kr(440, 1760, 1)), mul: 0.1)}.play
