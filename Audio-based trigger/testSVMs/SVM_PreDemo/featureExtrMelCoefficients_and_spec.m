%this function returns computed MFCC coeficients for individual file

function [featureVector,labels] = featureExtrMelCoefficients_and_spec(audio,fs,audioname,vectorClass)

if(length(audio) < 0.3*fs)
	featureVector = [];
	labels = [];
	return;
end

% making sure that audio vector is row alligned
[ar,ac] = size(audio);	
if(ar<ac)
	audio = audio';
end

% convert to mono if stereo
if(size(audio,2)==2)
	audio = sum(audio,2); 		% perform row wise sum
end
idx = find(audio);
audio = audio(idx(1):end);

% find number of 2 sec samples
sig_len = length(audio);
samp_len = 1.98*fs;
overlap = samp_len;
nSamples= floor(sig_len/overlap);	% this will initialise nSample to the number of
					% 2 sec samples in that wav file

%initialize variables
featureVector = [];
labels = [];	
feat_count = 1;

if (sig_len <= samp_len)
	
	[segments, fs] = detectVoiced(audio,fs);
	if(isempty(segments))
		featureVector = [];
		labels = [];
		%display('small empty file');
		return;
	end
	segSig = [];
	for i = 1:length(segments)
		segSig = [segSig; segments{i}];
	end
	if(size(segSig,2)~=1)
		disp(size(audio));
		disp(size(segSig));
		disp(size(segments));
		error('segSig error');
	end
	audio = segSig;

	%normalising
	audio = audio ./ (max(abs(audio)));
	
	mfc_vec = feature_extr_mfcc(audio,fs);
	spec_vec = feature_extr_spec(audio,fs,0.050,0.025);
	%display(mfc_vec);

	featureVector(feat_count,:)= [mfc_vec,spec_vec,vectorClass];
	labels{feat_count} = strcat(audioname);
	return;
end

samp1 = 1;
samp2 = samp_len;		% remember samp_len is set to 1.98*fs on top
for for_all_samples = 1: nSamples
	sample = audio(samp1:samp2);

	[segments,fs] = detectVoiced(sample,fs);
	if(isempty(segments))
		%display('empty file');
		samp1 = samp1 + overlap;
		samp2 = samp2 + overlap;
		if(samp2 > sig_len)
			if(samp1 > (sig_len-samp_len/2))
				break;
			else
				samp2 = sig_len;	
			end
		end
		continue;
	end
	segSig =[];
	for i =1: length(segments)
		segSig = [segSig; segments{i}];
	end
	if(size(segSig,2)~=1)
        	disp(size(segSig)); 
        	disp(size(segments));
        	error('segSig error');
   	end
    	sample = segSig;
	
	%normalising
	sample = sample ./ (max(abs(sample)));

	mfc_vec = feature_extr_mfcc(sample,fs);
	spec_vec = feature_extr_spec(sample,fs,0.050,0.025);
	%display(mfc_vec);

	featureVector(feat_count,:)= [mfc_vec,spec_vec,vectorClass];
	labels{feat_count} = strcat(audioname,'_',num2str(for_all_samples));
	feat_count = feat_count +1;

	samp1 = samp1 + overlap;
	samp2 = samp2 + overlap;
	if(samp2 > sig_len)
		if(samp1 > (sig_len-samp_len/2))
			break;
		else
			samp2 = sig_len;
		end
	end
end

%-----------------------------------------------------------------------
% extract spectral feature for single audio	
%-----------------------------------------------------------------------
function feature_vec = feature_extr_spec(signal,fs, win,step)

% convert window length and step from seconds to samples:
if(size(signal,1)==1)
	signal = signal';
end
windowLength = round(win * fs);
step = round(step * fs);

curPos = 1;
L = length(signal);

% compute the total number of frames:
numOfFrames = floor((L-windowLength)/step)+ 1;
% number of features to be computed:
numOfFeatures = 8;
Features = zeros(numOfFeatures, numOfFrames);
Ham = window(@hamming, windowLength);

for i=1:numOfFrames  %for each frame
	%get current frame:
	frame = signal(curPos:curPos+windowLength-1);
	frame = frame .* Ham;
	frameFFT = getDFT(frame,fs);

	if(sum(abs(frame))>eps)
		if(i == 1)
			frameFFTPrev = frameFFT;
		end
		
		%following function returns spectral centroid and spectral spread normalised to 0..1
		[Features(1,i) Features(2,i)]= feature_spectral_centroid(frameFFT,fs);
		
		Features(3,i) = feature_spectral_entropy(frameFFT, 10);
		Features(4,i) = feature_spectral_flux(frameFFT, frameFFTPrev);
		Features(5,i) = feature_spectral_rolloff(frameFFT, 0.90);
		[HR, F0] = feature_harmonic(frame,fs);
		Features(6,i) = HR;			% Harmonic Ratio
		Features(7,i) = F0;			% Fundamental Frequency
		Features(8,i) = spectral_entropy(frameFFT); %actually computes spectral flatness
	else
		Features(:,i) = zeros(numOfFeatures,1);
	end
	
	curPos = curPos +step;
	frameFFTPrev = frameFFT;
end
meanF = mean(Features,2);
stdF = std(Features,0,2);
feature_vec = [meanF', stdF'];


%-----------------------------------------------------------------------
%extract MFCC feature for single audio
%-----------------------------------------------------------------------
function mfcc_vector = feature_extr_mfcc(sample,fs)
mfc = melcepst(sample,fs,'0');
mfc(any(mfc == 0,2),:) = [];
mfcc_vector = [mean(mfc,1), std(mfc,0,1)];%std(x,opt,dim) --->opt- 0: normalize with N-1
					  %	   		   1: normalize with N

