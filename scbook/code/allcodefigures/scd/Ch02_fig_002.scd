/* Figure 2.2 */

Server.default = s = Server.internal.boot;

(
SynthDef(\UGen_ex2, {arg freq = 440;
	var src, compressor, limiter, out;
	// 10 SinOsc's, mixed together. Output amplitude is controlled with an Dust UGen
 	// wrapped in a Decay2 UGen to create a spike with an Exponential Decay
	src = SinOsc.ar(
		// a harmonic series based on freq
		Array.series(10, freq, freq),
		0, // phase
		Array.fill(10, {Decay2.ar(
			// Dust will create an impulse about every 2 seconds, with values between 0
			// and 5
			Dust.ar(0.1, 5),
			// Decay2, attach time of 0.01 seconds and a decay time of 5 seconds to
			// allow for a build up of signal
			0.01, 5)});
		).sum;

	// compress signal about 0.5
	//compressor = Compander.ar(src, src, 0.5, 1, 0.1);
	// compress signal about 0.01
	compressor = Compander.ar(src, src, 0.01, 1, 0.1);

	limiter = Limiter.ar(compressor, 0.5);
	// out is the compressed only signal on the left, the compressed and limited on the
	// right
	out = [DelayN.ar(compressor, 0.02, 0.02), limiter];
	// use Peak and poll to track the highest output values. Updates every second
	Peak.ar(out ++  src, Impulse.kr(1)).poll(1, ["compressed", "limited", "src"]);
	Out.ar(0, out);
}).add;
)
z = s.scope; a = Synth(\UGen_ex2, [\freq, 440]);

a.free; z.window.close;

Array.series(10, 440, 400).postln;
Array.series(size: 10, start: 440, step: 400).postln;
//[ 440, 880, 1320, 1760, 2200, 2640, 3080, 3520, 3960, 4400 ]
//[ 440, 880, 1320, 1760, 2200, 2640, 3080, 3520, 3960, 4400 ]

Array.series(10, 440).postln;
//[ 440, 441, 442, 443, 444, 445, 446, 447, 448, 449 ]
