(
// run this first
a = SynthDef("PMCrotale",
{
arg rate = 9;
var trigger, freq, latchrate, index, ratio, env, out;

latchrate = rate * LFNoise0.kr(1/10, mul: 0.03, add: 1.6);
latchrate.poll(Impulse.kr(1/10), "latchrate");

trigger = Impulse.kr(rate);
		// index = Latch.kr(
		// 	LFSaw.kr(latchrate, mul: 5, add: 6),
		// 	trigger
		// );
index = LFNoise1.kr(rate, mul: 5, add: 6);
index.poll(trigger, "index");

		// freq = Latch.kr(
		// 	LFSaw.kr(latchrate,
		// 		mul: max(0, LFNoise1.kr(1/5, mul: 24, add: 10)),
		// 	add: LFNoise0.kr(1/7, mul: 12, add: 60)),
		// 	trigger
		// ).round(1).midicps;

		// freq = LFNoise1.kr(rate, mul: 24, add: 60 - 36).round(1).midicps;
    freq = LFNoise1.kr(rate, mul: 1, add: 60 - 48 - 6).round(1).midicps;
		// freq = LFNoise1.kr(rate, mul: 48, add: 60).round(1).midicps;

freq.poll(trigger, "freq");

ratio = LFNoise1.kr(1/10, mul: 2.0, add: 5.0);
ratio.poll(Impulse.kr(1/10), "ratio");

env = EnvGen.kr(Env.perc(0, 1.0 / rate), doneAction:2);

out = PMOsc.ar(
	[freq, freq * 1.5],
	freq * ratio,
	index,
	mul: env * 0.1
);

Out.ar(0, out); //Out.ar(bus, out);
}
).add
)

//Then run this a bunch of times:
Synth("PMCrotale", ["rate", 9])

// This uses the PMCrotale synth definition
(
r = Task({
	inf.do({ arg count;
		Synth("PMCrotale", ["rate", 9]);
		(1.0 / 9).wait;
	});
}).start
)

r.stop; // run this to stop
