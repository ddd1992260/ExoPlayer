package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.extractor.flv.ClientFlvExtractor;
import com.google.android.exoplayer2.extractor.mkv.MatroskaExtractor;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import com.google.android.exoplayer2.extractor.mp4.Mp4Extractor;
import com.google.android.exoplayer2.extractor.ogg.OggExtractor;
import com.google.android.exoplayer2.extractor.ts.Ac3Extractor;
import com.google.android.exoplayer2.extractor.ts.AdtsExtractor;
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader;
import com.google.android.exoplayer2.extractor.wav.WavExtractor;
import com.google.android.exoplayer2.util.TimestampAdjuster;

import java.lang.reflect.Constructor;

/**
 * Created by zhaodedong on 17/9/12.
 */

public class ClientDefaultExtractorsFactory implements ExtractorsFactory {

    private static final Constructor<? extends Extractor> FLAC_EXTRACTOR_CONSTRUCTOR;
    static {
        Constructor<? extends Extractor> flacExtractorConstructor = null;
        try {
            flacExtractorConstructor =
                    Class.forName("com.google.android.exoplayer2.ext.flac.FlacExtractor")
                            .asSubclass(Extractor.class).getConstructor();
        } catch (ClassNotFoundException e) {
            // Extractor not found.
        } catch (NoSuchMethodException e) {
            // Constructor not found.
        }
        FLAC_EXTRACTOR_CONSTRUCTOR = flacExtractorConstructor;
    }

    private @MatroskaExtractor.Flags int matroskaFlags;
    private @FragmentedMp4Extractor.Flags int fragmentedMp4Flags;
    private @Mp3Extractor.Flags int mp3Flags;
    private @TsExtractor.Mode int tsMode;
    private @DefaultTsPayloadReaderFactory.Flags int tsFlags;

    public ClientDefaultExtractorsFactory() {
        tsMode = TsExtractor.MODE_SINGLE_PMT;
    }

    /**
     * Sets flags for {@link MatroskaExtractor} instances created by the factory.
     *
     * @see MatroskaExtractor#MatroskaExtractor(int)
     * @param flags The flags to use.
     * @return The factory, for convenience.
     */
    public synchronized ClientDefaultExtractorsFactory setMatroskaExtractorFlags(
            @MatroskaExtractor.Flags int flags) {
        this.matroskaFlags = flags;
        return this;
    }

    /**
     * Sets flags for {@link FragmentedMp4Extractor} instances created by the factory.
     *
     * @see FragmentedMp4Extractor#FragmentedMp4Extractor(int)
     * @param flags The flags to use.
     * @return The factory, for convenience.
     */
    public synchronized ClientDefaultExtractorsFactory setFragmentedMp4ExtractorFlags(
            @FragmentedMp4Extractor.Flags int flags) {
        this.fragmentedMp4Flags = flags;
        return this;
    }

    /**
     * Sets flags for {@link Mp3Extractor} instances created by the factory.
     *
     * @see Mp3Extractor#Mp3Extractor(int)
     * @param flags The flags to use.
     * @return The factory, for convenience.
     */
    public synchronized ClientDefaultExtractorsFactory setMp3ExtractorFlags(@Mp3Extractor.Flags int flags) {
        mp3Flags = flags;
        return this;
    }

    /**
     * Sets the mode for {@link TsExtractor} instances created by the factory.
     *
     * @see TsExtractor#TsExtractor(int, TimestampAdjuster, TsPayloadReader.Factory)
     * @param mode The mode to use.
     * @return The factory, for convenience.
     */
    public synchronized ClientDefaultExtractorsFactory setTsExtractorMode(@TsExtractor.Mode int mode) {
        tsMode = mode;
        return this;
    }

    /**
     * Sets flags for {@link DefaultTsPayloadReaderFactory}s used by {@link TsExtractor} instances
     * created by the factory.
     *
     * @see TsExtractor#TsExtractor(int)
     * @param flags The flags to use.
     * @return The factory, for convenience.
     */
    public synchronized ClientDefaultExtractorsFactory setTsExtractorFlags(
            @DefaultTsPayloadReaderFactory.Flags int flags) {
        tsFlags = flags;
        return this;
    }

    @Override
    public synchronized Extractor[] createExtractors() {
        Extractor[] extractors = new Extractor[FLAC_EXTRACTOR_CONSTRUCTOR == null ? 11 : 12];
        extractors[0] = new MatroskaExtractor(matroskaFlags);
        extractors[1] = new FragmentedMp4Extractor(fragmentedMp4Flags);
        extractors[2] = new Mp4Extractor();
        extractors[3] = new Mp3Extractor(mp3Flags);
        extractors[4] = new AdtsExtractor();
        extractors[5] = new Ac3Extractor();
        extractors[6] = new TsExtractor(tsMode, tsFlags);
        extractors[7] = new ClientFlvExtractor();
        extractors[8] = new OggExtractor();
        extractors[9] = new PsExtractor();
        extractors[10] = new WavExtractor();
        if (FLAC_EXTRACTOR_CONSTRUCTOR != null) {
            try {
                extractors[11] = FLAC_EXTRACTOR_CONSTRUCTOR.newInstance();
            } catch (Exception e) {
                // Should never happen.
                throw new IllegalStateException("Unexpected error creating FLAC extractor", e);
            }
        }
        return extractors;
    }

}

