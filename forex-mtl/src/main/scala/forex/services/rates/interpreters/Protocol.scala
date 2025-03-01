package forex.services.rates.interpreters

import cats.implicits.showInterpolator
import forex.domain.Currency
import forex.domain.Rate.Pair
import forex.http.rates.Protocol.currencyDecoder
import io.circe.Decoder
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder
import java.time.OffsetDateTime
import org.http4s.{QueryParamEncoder, QueryParameterValue}

object Protocol {

  implicit val configuration: Configuration = Configuration.default.withSnakeCaseMemberNames

  case class OneFrame(
      from: Currency,
      to: Currency,
      bid: BigDecimal,
      ask: BigDecimal,
      price: BigDecimal,
      timeStamp: OffsetDateTime
  )

  implicit val pairQueryParam: QueryParamEncoder[Pair] =
    (pair: Pair) => QueryParameterValue(show"${pair.from}${pair.to}")

  implicit val oneFrameDecoder: Decoder[OneFrame] =
    deriveConfiguredDecoder[OneFrame]
}