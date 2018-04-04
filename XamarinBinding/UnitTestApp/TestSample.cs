using NUnit.Framework;
using ThcompHAL = JP.CO.Thcomp.Http_abstract_layer;


namespace UnitTestApp
{
    [TestFixture]
    public class TestsSample
    {

        [SetUp]
        public void Setup() { }


        [TearDown]
        public void Tear() { }

        [Test]
        public void TestHttpAccessLayer1()
        {
            ThcompHAL.HttpAccessLayer accessLayer = ThcompHAL.HttpAccessLayer.GetInstance(Android.App.Application.Context, ThcompHAL.HttpAccessLayer.Accessor.OkHttp);
            accessLayer.ResponseCallback(
                delegate (ThcompHAL.IResponse successResponse)
                {
                    Assert.True(successResponse != null);
                    Assert.True(successResponse.StatusCode == 200);
                    Assert.True(successResponse.ReasonPhrase != null);
                    Assert.True(successResponse.GetHeaders(null).Count > 0);
                    Assert.True(successResponse.GetHeaders("Content-Length").Count > 0);
                    Assert.True(successResponse.MimeType.StartsWith("text/html"));
                },
                delegate (ThcompHAL.IResponse failResponse)
                {
                    Assert.True(failResponse != null);
                    Assert.True(failResponse.StatusCode != 200);
                }
            ).Uri("https://www.google.co.jp").Get();

            accessLayer.ResponseCallback((ThcompHAL.HttpAccessLayer.SuccessResponse)
                delegate (ThcompHAL.IResponse successResponse)
                {
                    Assert.True(successResponse != null);
                    Assert.True(successResponse.StatusCode == 200);
                    Assert.True(successResponse.ReasonPhrase != null);
                    Assert.True(successResponse.GetHeaders(null).Count > 0);
                    Assert.True(successResponse.GetHeaders("Content-Length").Count > 0);
                    Assert.True(successResponse.MimeType.StartsWith("text/html"));
                }
            ).Uri("https://www.google.co.jp").Get();

            accessLayer.ResponseCallback((ThcompHAL.HttpAccessLayer.FailResponse)
                delegate (ThcompHAL.IResponse failResponse)
                {
                    Assert.True(false);
                }
            ).Uri("https://www.google.co.jp").Get();
        }

        [Test]
        public void TestHttpAccessLayer2()
        {
            ThcompHAL.HttpAccessLayer accessLayer = ThcompHAL.HttpAccessLayer.GetInstance(Android.App.Application.Context, ThcompHAL.HttpAccessLayer.Accessor.OkHttp);
            accessLayer.ResponseCallback(
                delegate (ThcompHAL.IResponse successResponse)
                {
                    Assert.True(successResponse != null);
                    Assert.True(successResponse.StatusCode == 200);
                    Assert.True(successResponse.ReasonPhrase != null);
                    Assert.True(successResponse.GetHeaders(null).Count > 0);
                    Assert.True(successResponse.GetHeaders("Content-Length").Count > 0);
                    Assert.True(successResponse.MimeType.StartsWith("text/html"));
                },
                delegate (ThcompHAL.IResponse failResponse)
                {
                    Assert.True(failResponse != null);
                    Assert.True(failResponse.StatusCode != 200);
                }
            ).Uri("https://www.google.co.jp")
            .RequestParam(new ThcompHAL.StringRequestParameter("", "nanikaireteokimasyou")).Post();

            accessLayer.ResponseCallback((ThcompHAL.HttpAccessLayer.SuccessResponse)
                delegate (ThcompHAL.IResponse successResponse)
                {
                    Assert.True(false);
                }
            ).Uri("https://www.google.co.jp")
            .RequestParam(new ThcompHAL.StringRequestParameter("", "nanikaireteokimasyou")).Post();

            accessLayer.ResponseCallback((ThcompHAL.HttpAccessLayer.FailResponse)
                delegate (ThcompHAL.IResponse failResponse)
                {
                    Assert.True(failResponse != null);
                    Assert.True(failResponse.StatusCode != 200);
                }
            ).Uri("https://www.google.co.jp")
            .RequestParam(new ThcompHAL.StringRequestParameter("", "nanikaireteokimasyou")).Post();
        }

        [Test]
        public void TestHttpAccessLayer3()
        {
            ThcompHAL.HttpAccessLayer accessLayer = ThcompHAL.HttpAccessLayer.GetInstance(Android.App.Application.Context, ThcompHAL.HttpAccessLayer.Accessor.OkHttp);
            accessLayer.ResponseCallback(
                delegate (ThcompHAL.IResponse successResponse)
                {
                    Assert.True(successResponse != null);
                    Assert.True(successResponse.StatusCode == 200);
                    Assert.True(successResponse.ReasonPhrase != null);
                    Assert.True(successResponse.GetHeaders(null).Count > 0);
                    Assert.True(successResponse.GetHeaders("Content-Length").Count > 0);
                    Assert.True(successResponse.MimeType.StartsWith("text/html"));
                },
                delegate (ThcompHAL.IResponse failResponse)
                {
                    Assert.True(failResponse != null);
                    Assert.True(failResponse.StatusCode != 200);
                }
            ).Uri("https://www.konnna.domainga.naikotowo.negaimasu").Get();

            accessLayer.ResponseCallback((ThcompHAL.HttpAccessLayer.SuccessResponse)
                delegate (ThcompHAL.IResponse successResponse)
                {
                    Assert.True(false);
                }
            ).Uri("https://www.konnna.domainga.naikotowo.negaimasu").Get();

            accessLayer.ResponseCallback((ThcompHAL.HttpAccessLayer.FailResponse)
                delegate (ThcompHAL.IResponse failResponse)
                {
                    Assert.True(failResponse != null);
                    Assert.True(failResponse.StatusCode != 200);
                }
            ).Uri("https://www.konnna.domainga.naikotowo.negaimasu").Get();
        }
    }
}