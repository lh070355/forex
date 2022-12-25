package forex.services.rates.interpreters

import cats.Applicative
import cats.effect.Async
import cats.implicits._
import forex.config.OneFrameClientConfig
import forex.domain.{ Price, Rate, Timestamp }
import forex.services.rates.Algebra
import forex.services.rates.errors._
import forex.services.rates.interpreters.OneFrameLive.{ convert, getPairRatesRequest, unique }
import Protocol._
import org.http4s.Method.GET
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.client.Client
import org.http4s.{ Header, Headers, Request, Uri }

class OneFrameLive[F[_]: Async](config: OneFrameClientConfig, client: Client[F]) extends Algebra[F] {

  override def get(pair: Rate.Pair): F[Error Either Rate] = {

    val request = getPairRatesRequest[F](config.uri, config.token, pair)

    client.run(request).use { response =>
      response
        .as[List[OneFrame]]
        .flatMap(unique[F, OneFrame](_, Error.OneFrameLookupFailed(s"$pair not found")))
        .map(_.map(convert))
    }
  }
}

object OneFrameLive {

  def convert(frame: OneFrame): Rate =
    Rate(
      Rate.Pair(frame.from, frame.to),
      Price(frame.price),
      Timestamp(frame.timeStamp)
    )

  def unique[F[_]: Applicative, A](values: List[A], ifNone: Error): F[Error Either A] =
    values match {
      case head :: _ => Applicative[F].pure(Right(head))
      case Nil       => Applicative[F].pure(Left(ifNone))
    }

  def getPairRatesRequest[F[_]](uri: Uri, token: String, pair: Rate.Pair): Request[F] = {

    val path = uri / "rates" withQueryParam("pair", pair)

    Request[F](GET, path).withHeaders(Headers.of(Header("token", token)))
  }

}