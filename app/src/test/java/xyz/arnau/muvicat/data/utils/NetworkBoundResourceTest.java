package xyz.arnau.muvicat.data.utils;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import xyz.arnau.muvicat.AppExecutors;
import xyz.arnau.muvicat.data.model.Resource;
import xyz.arnau.muvicat.data.utils.NetworkBoundResource;
import xyz.arnau.muvicat.remote.model.Response;
import xyz.arnau.muvicat.remote.model.ResponseStatus;
import xyz.arnau.muvicat.utils.ApiUtil;
import xyz.arnau.muvicat.utils.CountingAppExecutors;
import xyz.arnau.muvicat.utils.InstantAppExecutors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SuppressWarnings("ALL")
@RunWith(Parameterized.class)
public class NetworkBoundResourceTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private Function<Response<Foo>, Void> saveResponse;

    private Function<Void, Boolean> shouldFetch;

    private Function<Void, LiveData<Response<Foo>>> createCall;

    private MutableLiveData<Foo> dbData = new MutableLiveData<>();

    private NetworkBoundResource<Foo> networkBoundResource;

    private AtomicBoolean fetchedOnce = new AtomicBoolean(false);
    private CountingAppExecutors countingAppExecutors;
    private final boolean useRealExecutors;

    @Parameterized.Parameters
    public static List<Boolean> param() {
        return Arrays.asList(true, false);
    }

    public NetworkBoundResourceTest(boolean useRealExecutors) {
        this.useRealExecutors = useRealExecutors;
        if (useRealExecutors) {
            countingAppExecutors = new CountingAppExecutors();
        }
    }

    @Before
    public void init() {
        AppExecutors appExecutors = useRealExecutors
                ? countingAppExecutors.getAppExecutors()
                : new InstantAppExecutors();
        networkBoundResource = new NetworkBoundResource<Foo>(appExecutors) {
            @NotNull
            @Override
            protected LiveData<Response<Foo>> createCall() {
                return createCall.apply(null);
            }

            @NotNull
            @Override
            protected LiveData<Foo> loadFromDb() {
                return dbData;
            }

            @Override
            protected boolean shouldFetch() {
                return shouldFetch.apply(null) && fetchedOnce.compareAndSet(false, true);
            }

            @Override
            protected void saveResponse(@NotNull Response<Foo> item) {
                saveResponse.apply(item);
            }
        };
    }

    private void drain() {
        if (!useRealExecutors) {
            return;
        }
        try {
            countingAppExecutors.drainTasks(1, TimeUnit.SECONDS);
        } catch (Throwable t) {
            throw new AssertionError(t);
        }
    }

    @Test
    public void basicFromNetwork() {
        AtomicReference<Foo> saved = new AtomicReference<>();
        shouldFetch = foo -> true;
        Foo fetchedDbValue = new Foo(1);
        saveResponse = foo -> {
            saved.set(foo.getBody());
            dbData.setValue(fetchedDbValue);
            return null;
        };
        final Foo networkResult = new Foo(1);
        createCall = aVoid -> ApiUtil.Companion.successCall(networkResult);

        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        drain();
        verify(observer).onChanged(Resource.Companion.loading(null));
        reset(observer);
        dbData.setValue(null);
        drain();
        Assert.assertThat(saved.get(), Matchers.is(networkResult));
        verify(observer).onChanged(Resource.Companion.success(fetchedDbValue));
    }

    @Test
    public void failureFromNetwork() {
        AtomicBoolean saved = new AtomicBoolean(false);
        shouldFetch = foo -> true;
        saveResponse = foo -> {
            saved.set(true);
            return null;
        };
        Response response = new Response(null, "ERROR MESSAGE", ResponseStatus.ERROR, null);
        createCall = (aVoid) -> ApiUtil.Companion.createCall(response);

        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        drain();
        verify(observer).onChanged(Resource.Companion.loading(null));
        reset(observer);
        dbData.setValue(null);
        drain();
        assertThat(saved.get(), Matchers.is(false));
        verify(observer).onChanged(Resource.Companion.error("ERROR MESSAGE", null));
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void dbSuccessWithoutNetwork() {
        AtomicBoolean saved = new AtomicBoolean(false);
        shouldFetch = foo -> false;
        saveResponse = foo -> {
            saved.set(true);
            return null;
        };
        createCall = (aVoid) -> null;

        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        drain();
        verify(observer).onChanged(Resource.Companion.loading(null));
        reset(observer);
        Foo dbFoo = new Foo(1);
        dbData.setValue(dbFoo);
        drain();
        verify(observer).onChanged(Resource.Companion.success(dbFoo));
        assertThat(saved.get(), Matchers.is(false));
        Foo dbFoo2 = new Foo(2);
        dbData.setValue(dbFoo2);
        drain();
        verify(observer).onChanged(Resource.Companion.success(dbFoo2));
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void dbSuccessWithoutNetworkDbUpdatedButNoChange() {
        AtomicBoolean saved = new AtomicBoolean(false);
        shouldFetch = foo -> false;
        saveResponse = foo -> {
            saved.set(true);
            return null;
        };
        createCall = (aVoid) -> null;

        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        drain();
        verify(observer).onChanged(Resource.Companion.loading(null));
        reset(observer);
        Foo dbFoo = new Foo(1);
        dbData.setValue(dbFoo);
        drain();
        verify(observer).onChanged(Resource.Companion.success(dbFoo));
        assertThat(saved.get(), Matchers.is(false));
        dbData.setValue(dbFoo);
        drain();
        verify(observer).onChanged(Resource.Companion.success(dbFoo));
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void dbSuccessWithFetchFailure() {
        Foo dbValue = new Foo(1);
        AtomicBoolean saved = new AtomicBoolean(false);
        shouldFetch = foo -> true;
        saveResponse = foo -> {
            saved.set(true);
            return null;
        };
        String errorBody = "error";
        MutableLiveData<Response<Foo>> apiResponseLiveData = new MutableLiveData<>();
        createCall = aVoid -> apiResponseLiveData;

        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        drain();
        verify(observer).onChanged(Resource.Companion.loading(null));
        reset(observer);

        dbData.setValue(dbValue);
        drain();
        verify(observer).onChanged(Resource.Companion.loading(dbValue));


        Response response = new Response<Foo>(null, errorBody, ResponseStatus.ERROR, null);
        apiResponseLiveData.setValue(response);
        drain();
        assertThat(saved.get(), Matchers.is(false));
        verify(observer).onChanged(Resource.Companion.error(errorBody, dbValue));

        Foo dbValue2 = new Foo(2);
        dbData.setValue(dbValue2);
        drain();
        verify(observer).onChanged(Resource.Companion.error(errorBody, dbValue2));
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void dbSuccessWithFetchNotModified() {
        Foo dbValue = new Foo(1);
        AtomicBoolean saved = new AtomicBoolean(false);
        shouldFetch = foo -> true;
        saveResponse = foo -> {
            saved.set(true);
            return null;
        };
        MutableLiveData<Response<Foo>> apiResponseLiveData = new MutableLiveData<>();
        createCall = aVoid -> apiResponseLiveData;

        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        drain();
        verify(observer).onChanged(Resource.Companion.loading(null));
        reset(observer);

        dbData.setValue(dbValue);
        drain();
        verify(observer).onChanged(Resource.Companion.loading(dbValue));


        Response response = new Response<Foo>(null, null, ResponseStatus.NOT_MODIFIED, null);
        apiResponseLiveData.setValue(response);
        drain();
        assertThat(saved.get(), Matchers.is(true));
        verify(observer).onChanged(Resource.Companion.success(dbValue));

        Foo dbValue2 = new Foo(2);
        dbData.setValue(dbValue2);
        drain();
        verify(observer).onChanged(Resource.Companion.success(dbValue2));
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void dbSuccessWithReFetchSuccess() {
        Foo dbValue = new Foo(1);
        Foo dbValue2 = new Foo(1);
        AtomicReference<Foo> saved = new AtomicReference<>();
        shouldFetch = foo -> true;
        saveResponse = foo -> {
            saved.set(foo.getBody());
            dbData.setValue(dbValue2);
            return null;
        };
        MutableLiveData<Response<Foo>> apiResponseLiveData = new MutableLiveData();
        createCall = aVoid -> apiResponseLiveData;

        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        drain();
        verify(observer).onChanged(Resource.Companion.loading(null));
        reset(observer);

        dbData.setValue(dbValue);
        drain();
        Foo networkResult = new Foo(1);
        verify(observer).onChanged(Resource.Companion.loading(dbValue));
        apiResponseLiveData.setValue(new Response<>(networkResult, null, ResponseStatus.SUCCESSFUL, null));
        drain();
        assertThat(saved.get(), Matchers.is(networkResult));
        verify(observer).onChanged(Resource.Companion.success(dbValue2));
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void dbSuccessWithReFetchSuccessButNullResponse() {
        Foo dbValue = new Foo(1);
        Foo dbValue2 = new Foo(1);
        AtomicReference<Foo> saved = new AtomicReference<>();
        shouldFetch = foo -> true;
        saveResponse = foo -> {
            saved.set(foo.getBody());
            return null;
        };
        MutableLiveData<Response<Foo>> apiResponseLiveData = new MutableLiveData();
        createCall = aVoid -> apiResponseLiveData;

        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        drain();
        verify(observer).onChanged(Resource.Companion.loading(null));
        reset(observer);

        dbData.setValue(dbValue);
        drain();
        Foo networkResult = new Foo(1);
        verify(observer).onChanged(Resource.Companion.loading(dbValue));
        apiResponseLiveData.setValue(null);
        drain();
        assertEquals(null, saved.get());
        verify(observer).onChanged(Resource.Companion.error(null, dbValue));
        verifyNoMoreInteractions(observer);
    }

    static class Foo {

        int value;

        Foo(int value) {
            this.value = value;
        }
    }
}