package com.secreto.data.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.utils.Logger;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MultipartRequest<T> extends Request<T> {
    private static final String TAG = MultipartRequest.class.getSimpleName();
    private Class<T> clazz;
    private MultipartEntity entity = new MultipartEntity();

    private Gson gson = new Gson();
    private Response.Listener<T> mListener;
    private File mFilePart;
    private ArrayList<File> mFileParts;
    private HashMap<String, File> fileHashMap;

    private HashMap<Object, Object> mParams;
    private String mFilePartName;
    private final Map<String, String> headers = new HashMap<>();

    private MultipartRequest(int method, String url, HashMap<Object, Object> params, final Class<T> clazz,
                             Response.ErrorListener errorListener, Response.Listener<T> listener) {
        super(method, url, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 25, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mListener = listener;
        mParams = params;
        this.clazz = clazz;
        buildMultipartEntity();
    }

    private MultipartRequest(String url, HashMap<Object, Object> params, final Class<T> clazz,
                             Response.ErrorListener errorListener,
                             Response.Listener<T> listener, String filePartName, File file) {
        super(Method.POST, url, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 25, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mListener = listener;
        mFilePart = file;
        mParams = params;
        mFilePartName = filePartName;
        this.clazz = clazz;
        buildMultipartEntity();
    }

    private MultipartRequest(String url, HashMap<Object, Object> params, final Class<T> clazz,
                             Response.ErrorListener errorListener,
                             Response.Listener<T> listener, String filePartName, ArrayList<File> files) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        mFileParts = files;
        mParams = params;
        mFilePartName = filePartName;
        this.clazz = clazz;
        buildMultipartEntity();
    }

    private MultipartRequest(String url, HashMap<Object, Object> params, final Class<T> clazz,
                             Response.ErrorListener errorListener,
                             Response.Listener<T> listener
            , HashMap<String, File> fileHashMaps) {
        super(Method.POST, url, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 25, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mListener = listener;
        fileHashMap = fileHashMaps;
        mParams = params;
        this.clazz = clazz;
        buildMultipartEntity();
    }

    public MultipartRequest(int method, String url, HashMap<Object, Object> params, final Class<T> clazz, final ResultListenerNG<T> resultListenerNG) {
        this(method, url, params, clazz, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resultListenerNG.onError(error);
            }
        }, new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                resultListenerNG.onSuccess(response);
            }
        });
    }

    public MultipartRequest(String url, HashMap<Object, Object> params, final Class<T> clazz,
                            final ResultListenerNG<T> resultListenerNG, String filePartName, File file) {
        this(url, params, clazz, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resultListenerNG.onError(error);
            }
        }, new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                resultListenerNG.onSuccess(response);
            }
        }, filePartName, file);
    }

    public MultipartRequest(String url, HashMap<Object, Object> params, final Class<T> clazz,
                            final ResultListenerNG<T> resultListenerNG, String filePartName, ArrayList<File> files) {
        this(url, params, clazz, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resultListenerNG.onError(error);
            }
        }, new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                resultListenerNG.onSuccess(response);
            }
        }, filePartName, files);
    }

    public MultipartRequest(String url, HashMap<Object, Object> params, final Class<T> clazz,
                            final ResultListenerNG<T> resultListenerNG, HashMap<String, File> fileHashMaps) {
        this(url, params, clazz, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resultListenerNG.onError(error);
            }
        }, new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                resultListenerNG.onSuccess(response);
            }
        }, fileHashMaps);
    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String utf8String = new String(response.data, "UTF-8");
//          String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Logger.d(TAG, "response : " + utf8String);
            T o = gson.fromJson(utf8String, clazz);
            if (o instanceof BaseResponse) {
                ((BaseResponse) o).setStatusCode(response.statusCode);
            }
            return Response.success(o, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    private void buildMultipartEntity() {
        // Adding image file to request in form of byte array
        if (mFilePart != null) {
            try {
                ByteArrayBody byteArrayBody = new ByteArrayBody(readBytesFromFile(mFilePart), "image/jpg", mFilePart.getName());
                entity.addPart(mFilePartName, byteArrayBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mFileParts != null) {
            for (File file : mFileParts) {
                try {
                    ByteArrayBody byteArrayBody = new ByteArrayBody(readBytesFromFile(file), "image/jpg", file.getName());
                    entity.addPart(mFilePartName, byteArrayBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (fileHashMap != null) {
            Iterator it = fileHashMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                try {
                    String key = (String) pair.getKey();
                    File file = (File) pair.getValue();
                    ByteArrayBody byteArrayBody = new ByteArrayBody(readBytesFromFile(file), "image/jpg", file.getName());
                    entity.addPart(key, byteArrayBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                it.remove();
            }
        }

        if (this.mParams != null) {
            for (Map.Entry<Object, Object> entry : this.mParams.entrySet()) {
                if (entry.getValue() != null) {
                    try {
                        entity.addPart(entry.getKey().toString(), new StringBody(entry.getValue().toString()));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static byte[] readBytesFromFile(File file) throws IOException {
        // Get the size of the file
        final long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            throw new IOException("Could not completely read file " + file.getName() + " as it is too long (" + length + " bytes, max supported "
                    + Integer.MAX_VALUE + ")");
        }

        final InputStream is = new FileInputStream(file);
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            is.close();
            throw new IOException("Could not completely read file " + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
}
