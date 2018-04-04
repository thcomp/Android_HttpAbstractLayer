using System;

namespace JP.CO.Thcomp.Http_abstract_layer
{
    public partial class HttpAccessLayer
    {
        public delegate void SuccessResponse(IResponse response);
        public delegate void FailResponse(IResponse response);

        internal class LocalSuccessResponseCallback : Java.Lang.Object, ISuccessResponseCallback
        {
            internal SuccessResponse mSuccessResponseDelegator;

            internal LocalSuccessResponseCallback(SuccessResponse delegator)
            {
                mSuccessResponseDelegator = delegator ?? throw new ArgumentNullException("delegator == null");
            }

            public void OnSuccess(IResponse p0)
            {
                mSuccessResponseDelegator(p0);
            }
        }

        internal class LocalFailResponseCallback : Java.Lang.Object, IFailResponseCallback
        {
            internal FailResponse mFailResponseDelegator;

            internal LocalFailResponseCallback(FailResponse delegator)
            {
                mFailResponseDelegator = delegator ?? throw new ArgumentNullException("delegator == null");
            }

            public void OnFail(IResponse p0)
            {
                mFailResponseDelegator(p0);
            }
        }

        internal class LocalResponseCallback : Java.Lang.Object, IResponseCallback
        {
            internal SuccessResponse mSuccessResponseDelegator;
            internal FailResponse mFailResponseDelegator;

            public LocalResponseCallback(SuccessResponse successResponseDelegator, FailResponse failResponseDelegator)
            {
                mSuccessResponseDelegator = successResponseDelegator ?? throw new ArgumentNullException("successResponseDelegator == null");
                mFailResponseDelegator = failResponseDelegator ?? throw new ArgumentNullException("failResponseDelegator == null");
            }

            public void OnFail(IResponse p0)
            {
                mFailResponseDelegator(p0);
            }

            public void OnSuccess(IResponse p0)
            {
                mSuccessResponseDelegator(p0);
            }
        }

        public HttpAccessLayer ResponseCallback(SuccessResponse delegator)
        {
            if (delegator == null)
            {
                ResponseCallback((IResponseCallback)null);
            }
            else
            {
                ResponseCallback(new LocalSuccessResponseCallback(delegator));
            }
            return this;
        }

        public HttpAccessLayer ResponseCallback(FailResponse delegator)
        {
            if (delegator == null)
            {
                ResponseCallback((IResponseCallback)null);
            }
            else
            {
                ResponseCallback(new LocalFailResponseCallback(delegator));
            }
            return this;
        }

        public HttpAccessLayer ResponseCallback(SuccessResponse successResponseDelegator, FailResponse failResponseDelegator)
        {
            if (successResponseDelegator == null && failResponseDelegator == null)
            {
                ResponseCallback((IResponseCallback)null);
            }
            else if (successResponseDelegator != null && failResponseDelegator == null)
            {
                ResponseCallback(new LocalSuccessResponseCallback(successResponseDelegator));
            }
            else if (successResponseDelegator == null && failResponseDelegator != null)
            {
                ResponseCallback(new LocalFailResponseCallback(failResponseDelegator));
            }
            else
            {
                ResponseCallback(new LocalResponseCallback(successResponseDelegator, failResponseDelegator));
            }
            return this;
        }
    }
}